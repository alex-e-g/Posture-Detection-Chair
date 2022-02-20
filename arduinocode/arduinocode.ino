#include <HTTPClient.h>
#include <HTTPUpdate.h>
#include <HTTPClientESP32Ex.h>
#include <WiFiClientSecureESP32.h>
#include <ssl_client32.h>
#include <FirebaseESP32.h>
#include <FB_HTTPClient32.h>
#include <FirebaseJson.h>
#include <WiFi.h>
#include "time.h"
#include <HX711_ADC.h>
#if defined(ESP8266)|| defined(ESP32) || defined(AVR)
#include <EEPROM.h>
#endif

FirebaseJson json;


//pins:
const int HX711_dout_1 = 12; //mcu > HX711 no 1 dout pin
const int HX711_sck_1 = 14; //mcu > HX711 no 1 sck pin
const int HX711_dout_2 = 26; //mcu > HX711 no 2 dout pin
const int HX711_sck_2 = 25; //mcu > HX711 no 2 sck pin
const int HX711_dout_3 = 32; //mcu > HX711 no 3 dout pin
const int HX711_sck_3 = 33; //mcu > HX711 no 3 sck pin

//HX711 constructor (dout pin, sck pin)
HX711_ADC LoadCell_1(HX711_dout_1, HX711_sck_1); //HX711 1
HX711_ADC LoadCell_2(HX711_dout_2, HX711_sck_2); //HX711 2
HX711_ADC LoadCell_3(HX711_dout_3, HX711_sck_3); //HX711 3

const int calVal_eepromAdress_1 = 0; 
const int calVal_eepromAdress_2 = 4;
const int calVal_eepromAdress_3 = 9;
unsigned long t = 0;



unsigned long currentMillis;
unsigned long previousMillis = 0;
const char* ntpServer = "pool.ntp.org";
const long gmtOffset_sec = 0; // Portuguese time zone
const int daylightOffset_sec = 3600;

//0. Optional: Variable where the data will be stored in Firebase
String path = "/ESP32_Device";

//1. Change the following info for WiFi connection

//const char* ssid = "BITnet";  
//const char* password = "12345678";

const char* ssid = "Mi A3";  
const char* password = "c0d2c770eb12";

//2. Firebase Project information (confidential!!)
// These variables were created in section 1
#define FIREBASE_HOST "https://postural-assessment-20efd-default-rtdb.europe-west1.firebasedatabase.app/" //Change to your Firebase RTDB project ID e.g. Your_Project_ID.firebaseio.com
#define FIREBASE_AUTH "iqrWfaMfQp1mvp8JvqDrgiht6qCaOfq5BmaLiN1h" //Change to your Firebase RTDB secret password

//3. Define FirebaseESP8266 data object for data sending and receiving
FirebaseData firebaseData;


void setup() {
  // To connect to WiFi 
  
  Serial.begin(115200);
  float calibrationValue_1; // calibration value load cell 1
  float calibrationValue_2;
  float calibrationValue_3;

#if defined(ESP8266) || defined(ESP32)
  EEPROM.begin(512); 
#endif
  EEPROM.get(calVal_eepromAdress_1, calibrationValue_1); // fetch the value from eeprom
  EEPROM.get(calVal_eepromAdress_2, calibrationValue_2);
  EEPROM.get(calVal_eepromAdress_3, calibrationValue_3);  
  
  LoadCell_1.begin();
  LoadCell_2.begin();
  LoadCell_3.begin();
  unsigned long stabilizingtime = 2000; // tare preciscion can be improved by adding a few seconds of stabilizing time
  boolean _tare = false; //set this to false if you don't want tare to be performed in the next step
  byte loadcell_1_rdy = 0;
  byte loadcell_2_rdy = 0;
  byte loadcell_3_rdy = 0;
  while ((loadcell_1_rdy + loadcell_2_rdy + loadcell_3_rdy) < 3) { //run startup, stabilization and tare, all modules simultaniously
    if (!loadcell_1_rdy) loadcell_1_rdy = LoadCell_1.startMultiple(stabilizingtime, _tare);
    if (!loadcell_2_rdy) loadcell_2_rdy = LoadCell_2.startMultiple(stabilizingtime, _tare);
    if (!loadcell_3_rdy) loadcell_3_rdy = LoadCell_3.startMultiple(stabilizingtime, _tare);
  }
  if (LoadCell_1.getTareTimeoutFlag()) {
    Serial.println("Timeout, check MCU>HX711 no.1 wiring and pin designations");
  }
  if (LoadCell_2.getTareTimeoutFlag()) {
    Serial.println("Timeout, check MCU>HX711 no.2 wiring and pin designations");
  }
  if (LoadCell_3.getTareTimeoutFlag()) {
    Serial.println("Timeout, check MCU>HX711 no.3 wiring and pin designations");
  }
  LoadCell_1.setCalFactor(calibrationValue_1); // user set calibration value (float)
  LoadCell_2.setCalFactor(calibrationValue_2); // user set calibration value (float)
  LoadCell_3.setCalFactor(calibrationValue_3); // user set calibration value (float)
  Serial.println("Startup is complete");

  //WiFi.config(ip);
  byte mac[6];                     // the MAC address of your Wifi shield

  WiFi.macAddress(mac);
  Serial.print("MAC: ");
  Serial.print(mac[5],HEX);
  Serial.print(":");
  Serial.print(mac[4],HEX);
  Serial.print(":");
  Serial.print(mac[3],HEX);
  Serial.print(":");
  Serial.print(mac[2],HEX);
  Serial.print(":");
  Serial.print(mac[1],HEX);
  Serial.print(":");
  Serial.println(mac[0],HEX);

  // attemwipt to connect to Wifi network:
  while ( WiFi.status() != WL_CONNECTED) {
    Serial.print("Attempting to connect to SSID: ");
    Serial.println(ssid);
    // Connect to WPA/WPA2 network. Change this line if using open or WEP network:    
    WiFi.begin(ssid, password);

    // wait 10 seconds for connection:
    delay(5000);
  }

  // print your WiFi shield's IP address:
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());
  
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();
    

  //5. Set your Firebase info
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  //6. Enable auto reconnect the WiFi when connection lost
  Firebase.reconnectWiFi(true);

  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);

}

void loop() {
//  // Saves LED status in a list
//  Firebase.pushDouble(firebaseData, path + "/LED_History",state);
//          
  currentMillis = millis();  
  if (currentMillis - previousMillis >= 1000) {
    if ( WiFi.status() == WL_CONNECTED){
      static boolean newDataReady = 0;
      const int serialPrintInterval = 0; //increase value to slow down serial print activity
    
      // check for new data/start next conversion:
      if (LoadCell_1.update()) newDataReady = true;
      LoadCell_2.update();
      LoadCell_3.update();
    
      //get smoothed value from data set
      if ((newDataReady)) {
        if (millis() > t + serialPrintInterval) {
          float a = LoadCell_1.getData();
          float b = LoadCell_2.getData();
          float c = LoadCell_3.getData();
          if (a>0 and a<50000 and b>0 and b<50000 and c>0 and c<50000) {
          String dateTime2 = printLocalTime();
          json.set("/t", dateTime2);
          json.set("/LB", a);
          json.set("/RB", b);
          json.set("/F", c);
          String path = "/ESP32_Device/";
          path = path + dateTime2; 
          Serial.println(path);
          Firebase.updateNode(firebaseData,path,json);
          
          Serial.print("Load_cell 1 output val: ");
          Serial.print(a);
          Serial.print("    Load_cell 2 output val: ");
          Serial.println(b);
          Serial.print("    Load_cell 3 output val: ");
          Serial.println(c);
          newDataReady = 0;
          t = millis();
        }
      }
      }
  
      // receive command from serial terminal, send 't' to initiate tare operation:
      if (Serial.available() > 0) {
        char inByte = Serial.read();
        if (inByte == 't') {
          LoadCell_1.tareNoDelay();
          LoadCell_2.tareNoDelay();
          LoadCell_3.tareNoDelay();
        }
      }
  
      //check if last tare operation is complete
      if (LoadCell_1.getTareStatus() == true) {
        Serial.println("Tare load cell 1 complete");
      }
      if (LoadCell_2.getTareStatus() == true) {
        Serial.println("Tare load cell 2 complete");
      }
      if (LoadCell_3.getTareStatus() == true) {
        Serial.println("Tare load cell 3 complete");
      } 
      previousMillis = currentMillis;
    }
    else {
      while ( WiFi.status() != WL_CONNECTED) {
      Serial.print("Attempting to connect to SSID: ");
      Serial.println(ssid);
      // Connect to WPA/WPA2 network. Change this line if using open or WEP network:    
      WiFi.begin(ssid, password);
  
      // wait 10 seconds for connection:
      delay(5000);
    }

  // print your WiFi shield's IP address:
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());
  
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();
    

  //5. Set your Firebase info
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  //6. Enable auto reconnect the WiFi when connection lost
  Firebase.reconnectWiFi(true);

  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);

    }
  }
}


String printLocalTime(){
  struct tm timeinfo;
  if(!getLocalTime(&timeinfo)){
    Serial.println("Failed to obtain time");
    return ".";
  }
  //Serial.println(&timeinfo, "%d %m %y %H:%M:%S");

  char dateTime[18]; // not sure about this part
  strftime(dateTime,18, "%d %m %y %H:%M:%S", &timeinfo);
  String date2 = String(dateTime);

  return date2;
}
