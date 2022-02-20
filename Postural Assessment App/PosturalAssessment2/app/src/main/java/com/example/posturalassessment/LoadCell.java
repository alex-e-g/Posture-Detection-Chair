package com.example.posturalassessment;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadCell implements Parcelable {
    double Cell_LB; // Left back cell
    double Cell_RB; // Right back cell
    double Cell_F; // front cell

    String time_point; // date and time format in string
    Date dateandtime;  // date and time format in calendar

    double CMx; // Centre of mass (horizontal)
    double CMy; // Centre of mass (vertical)

    double Left_Back_Ratio;
    double Right_Left_Ratio;
    double Normal_CMx = 13.50; // MAKE A BUTTON TO SET THIS
    double Normal_CMy = 4.5;

    double dBF = 21.82; // vertical distance between Back and Front Cells
    double dRF = 12.2; // horizontal distance for Front Cell
    double dRL = 24.4; // distance between Right and Left Back Cells

    double tolerance_margin = 2.0; // Maximum distance to ideal CM to consider good posture
    double sitting_tolerance = 1000; // Minimum load to consider person sitting

    Boolean GoodPosture; // How was the posture in this time point?
    Boolean Sitting; // Was the person sitting in this time point?


    public LoadCell() {
    }

    public LoadCell(double cell_LB, double cell_RB, String time_point, double cell_F) throws ParseException {

        this.Cell_LB =  cell_LB;
        this.Cell_RB =  cell_RB;
        this.Cell_F =  cell_F;
        this.time_point = time_point;
        this.dateandtime = createDate(time_point);

        // Calculate centre of mass based on 3 load cells (RB, RL and F)

        CMx = (Cell_RB + dRL * Cell_LB + dRF * Cell_F)/(Cell_RB + Cell_LB + Cell_F);
        CMy = (Cell_RB + Cell_LB + dBF * Cell_F)/(Cell_RB + Cell_LB + Cell_F);


        Sitting = isSitting();
        GoodPosture = EvaluatePostureCM();

    }


    protected LoadCell(Parcel in) {
        Cell_LB = in.readDouble();
        Cell_RB = in.readDouble();
        Cell_F = in.readDouble();
        time_point = in.readString();
        CMx = in.readDouble();
        CMy = in.readDouble();
        Left_Back_Ratio = in.readDouble();
        Right_Left_Ratio = in.readDouble();
        Normal_CMx = in.readDouble();
        Normal_CMy = in.readDouble();
        dBF = in.readDouble();
        dRF = in.readDouble();
        dRL = in.readDouble();
        tolerance_margin = in.readDouble();
        sitting_tolerance = in.readDouble();
        byte tmpGoodPosture = in.readByte();
        GoodPosture = tmpGoodPosture == 0 ? null : tmpGoodPosture == 1;
        byte tmpSitting = in.readByte();
        Sitting = tmpSitting == 0 ? null : tmpSitting == 1;
    }

    public static final Creator<LoadCell> CREATOR = new Creator<LoadCell>() {
        @Override
        public LoadCell createFromParcel(Parcel in) {
            return new LoadCell(in);
        }

        @Override
        public LoadCell[] newArray(int size) {
            return new LoadCell[size];
        }
    };


    public double getCell_F() {
        return Cell_F;
    }


    public double getCell_LB() {
        return Cell_LB;
    }

    public double getCell_RB() {
        return Cell_RB;
    }

    public String getTime_point() {
        return time_point;
    }

    public Date getDateandtime() {
        return dateandtime;
    }


    public double getCMx() {
        return CMx;
    }

    public double getCMy() {
        return CMy;
    }

   public double getLeft_Back_Ratio() {
        return Left_Back_Ratio;
    }

    public void setLeft_Back_Ratio(double left_Back_Ratio) {
        Left_Back_Ratio = left_Back_Ratio;
    }

    public double getRight_Left_Ratio() {
        return Right_Left_Ratio;
    }

    public void setRight_Left_Ratio(double right_Left_Ratio) {
        Right_Left_Ratio = right_Left_Ratio;
    }



    public Boolean EvaluatePostureCM(){
        GoodPosture = false;
        if(Math.abs(CMx - Normal_CMx) <= tolerance_margin &
                Math.abs(CMy - Normal_CMy) <= tolerance_margin){
            GoodPosture = true;
        }
        return GoodPosture;
    }


    public Boolean isSitting(){
        Sitting = true;

        if(Cell_F < sitting_tolerance & Cell_LB < sitting_tolerance & Cell_RB < sitting_tolerance)
            Sitting = false;

        return Sitting;
    }

    // Method to create the calendar points
    private Date createDate(String timepoint) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yy HH:mm:ss");
        Date date = sdf.parse(timepoint);
        return date;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.Cell_LB);
        dest.writeDouble(this.Cell_F);
        dest.writeDouble(this.Cell_RB);
        dest.writeString(time_point);
        dest.writeDouble(CMx);
        dest.writeDouble(CMy);
        dest.writeDouble(Left_Back_Ratio);
        dest.writeDouble(Right_Left_Ratio);
        dest.writeDouble(Normal_CMx);
        dest.writeDouble(Normal_CMy);
        dest.writeDouble(dBF);
        dest.writeDouble(dRF);
        dest.writeDouble(dRL);
        dest.writeDouble(tolerance_margin);
        dest.writeDouble(sitting_tolerance);
    }


}
