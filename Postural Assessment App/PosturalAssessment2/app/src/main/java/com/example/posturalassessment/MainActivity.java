package com.example.posturalassessment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.posturalassessment.views.CustomView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView mPostureStatus;
    private CustomView customView;
    private Date currentDateAndTime; // Current Time
    private Date lastPostureNotifTime = new Date(); // last time we sent a posture notif
    private Date lastSittingNotifTime = new Date(); // last time we sent a sitting notif
    private long notificationIntervalTime = 30;  // Do not send the same notification until after 30 seconds
    private long SittingTimeLimit = 60*60; // 1 hour sitting time limit max
    private int helper = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mPostureStatus = (TextView) findViewById(R.id.postureStatus);

        // Tab Layout Information
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);
        viewPager.bringToFront();

        tabLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment1(new fragment1(), "Home");
        vpAdapter.addFragment2(new fragment2(), "Stats");
        // vpAdapter.addFragment3(new fragment3(), "Help");
        //vpAdapter.addFragment(new fragment1(), "Home");
        //vpAdapter.addFragment(new fragment2(), "Stats");
        //vpAdapter.addFragment(new fragment3(), "Data");

        fragment2 fragment2 = vpAdapter.getFragment2();

        customView = (CustomView) findViewById(R.id.customView);

        new FireBaseDatabaseHelper().readLoadCells(new FireBaseDatabaseHelper.DataStatus() {


            @Override
            public void DataIsLoaded(ArrayList<LoadCell> loadcells, List<String> keys) {

                if (helper == 0){
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("loadcells", loadcells);

                    fragment2.setArguments(bundle);
                    //Bundle arguments = fragment2.getArguments();

                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.viewpager, fragment2.class, bundle)
                            .commit();

                    viewPager.setAdapter(vpAdapter);

                    helper = 1;
                }




                // Recycler View Information
                // new RecyclerView_Config().SetConfig(mRecyclerView, MainActivity.this, loadcells, keys);

                if(loadcells!= null) {
                    // Update the Posture Status
                    EvaluatePosture evaluatePosture = new EvaluatePosture(loadcells);
                    LoadCell current_cell = loadcells.get(loadcells.size()-1);

                    currentDateAndTime = new Date();
                    Date currentCellTime = loadcells.get(loadcells.size()-1).getDateandtime();

                    // If there is no data being sent after 5 seconds
                    if (currentDateAndTime.getTime() - currentCellTime.getTime() >= 5000){

                        // Update the colour of the load cells screen to grey
                        customView.paintcell(Color.GRAY, Color.GRAY, Color.GRAY);

                        // Display a 'not connected' message
                        mPostureStatus.setText("No Connection");
                        mPostureStatus.setBackgroundColor(Color.parseColor("#808080"));
                    }

                    // If we are still sending data
                    else{
                        // Update the colour of the load cells screen

                        double C1 = current_cell.getCell_RB();
                        double C2 = current_cell.getCell_LB();
                        double C3 = current_cell.getCell_F();

                        float hue_C1 = (float) -(C1 / 50000.0) * 100 + 100;
                        float hue_C2 = (float) -(C2 / 50000.0) * 100 + 100;
                        float hue_C3 = (float) -(C3 / 50000.0) * 100 + 100;

                        float[] hsv_1 = { hue_C1, 100f, 100f };
                        int C1_color = Color.HSVToColor(hsv_1);

                        float[] hsv_2 = { hue_C2, 100f, 100f };
                        int C2_color = Color.HSVToColor(hsv_2);

                        float[] hsv_3 = { hue_C3, 100f, 100f };
                        int C3_color = Color.HSVToColor(hsv_3);

                        customView.paintcell(C1_color, C2_color, C3_color);


                        // If the person is sitting down, get info about posture
                        if (evaluatePosture.getSitting()) {

                            // Update the screen view and send notifs about posture
                            if (evaluatePosture.getGood_Posture()) {
                                mPostureStatus.setText("Good");
                                mPostureStatus.setBackgroundColor(Color.parseColor("#FF8BC34A"));
                            }
                            if (!evaluatePosture.getGood_Posture()) {
                                mPostureStatus.setText("Bad");
                                mPostureStatus.setBackgroundColor(Color.parseColor("#F44336"));

                                // Send a notification if we haven't sent one in the last 30 seconds
                                long i = currentDateAndTime.getTime() - lastPostureNotifTime.getTime();
                                if (currentDateAndTime.getTime() - lastPostureNotifTime.getTime() >= notificationIntervalTime * 1000){
                                    pushNotification("Notification from Posture Assessment", "Please fix your posture!");
                                    lastPostureNotifTime = new Date();
                                }
                            }

                            // Send a notif if the person has been sitting for one hour

                            // If we haven't sent this notif in 30 seconds:
                            if (currentDateAndTime.getTime() - lastSittingNotifTime.getTime() >= notificationIntervalTime * 1000){

                                // If the person is still sitting
                                if (currentDateAndTime.getTime() - lastSittingNotifTime.getTime() >= SittingTimeLimit * 1000){
                                    // This step is necessary to not account for data from previous days
                                    // each time the app starts, we start counting the time
                                    // evaluatePosture.getSitting then looks at the load cells and removes the overall outliers to make sure
                                    if (evaluatePosture.TimeToGetUp()){
                                        pushNotification("Notification from Posture Assessment", "You have been sitting down for 1 hour. Consider taking a break now.");
                                        lastSittingNotifTime = new Date();
                                    }
                                }

                            }
                        }

                        // If the person is not sitting down, update screen
                        if (evaluatePosture.getSitting() == false){
                            mPostureStatus.setText("Not Sitting");
                            mPostureStatus.setBackgroundColor(Color.parseColor("#808080"));
                        }
                    }




                }

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });

    }

    // Method to send notifications
    private void pushNotification(String title, String body){

        int NOTIFICATION_ID = 234;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "my_channel_01";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body);

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }




}