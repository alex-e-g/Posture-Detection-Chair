package com.example.posturalassessment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment2 extends Fragment implements OnChartGestureListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView mExampleText;
    private ArrayList<LoadCell> loadCells;
    private BarChart barChart;
    private ArrayList<String> xAxisLabel = new ArrayList<>();

    public fragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment2 newInstance(String param1, String param2) {
        fragment2 fragment = new fragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment2, container, false);
        if (getArguments()!= null) {
            loadCells = getArguments().getParcelableArrayList("loadcells");
            LoadCell current_cell = loadCells.get(loadCells.size()-1);

        }


        barChart = (BarChart) v.findViewById(R.id.barchart);
        barChart.getDescription().setEnabled(false);
        barChart.setOnChartGestureListener(this);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);


        barChart.setData(generateBarData());


        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        barChart.getAxisRight().setEnabled(false);
        barChart.setFitBars(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.disableGridDashedLine();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);


        // Set XAxis as date format
        ValueFormatter formatter = new ValueFormatter() {


            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) value);
            }
        };
        xAxis.setValueFormatter(formatter);


        barChart.invalidate();


        return v;
    }

    private void loadBarChartData() {
        ArrayList<BarEntry> entries = new ArrayList<>();


    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START");
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END");
        barChart.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart long pressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart fling. VelocityX: " + velocityX + ", VelocityY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    protected BarData generateBarData() {

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<BarEntry> postureentries = new ArrayList<>();
        int previous_date = loadCells.get(0).getDateandtime().getDate();
        int sitting_counter = 0;  // number of seconds the person was sitting down
        int badposture_counter = 0; // number of seconds the person was in bad posture
        int date_counter = 0;

        for(int i = 0; i < loadCells.size(); i++) {

            int current_date = loadCells.get(i).getDateandtime().getDate();

            // If the date has changed
            if (previous_date != current_date){
                // Save the previous date bar data
                float datavalue = (float) sitting_counter; //(sitting_counter/3600.00);
                float posturedatavalue = (float) badposture_counter; // (badposture_counter/3600.00);
                entries.add(new BarEntry(date_counter, datavalue));
                postureentries.add(new BarEntry(date_counter, posturedatavalue));
                xAxisLabel.add(loadCells.get(i-1).getTime_point().substring(0, 8));

                // Update variables
                previous_date = current_date;
                sitting_counter = 0;
                badposture_counter = 0;
                date_counter +=1;
            }

            // If we are checking the same date
            if (previous_date == current_date){
                if (loadCells.get(i).isSitting()){
                    sitting_counter +=1;
                    if (loadCells.get(i).EvaluatePostureCM() == false) badposture_counter +=1;
                }

            }

            // Add the current date to the plot

            if (i==loadCells.size()-1){
                float datavalue = (float) sitting_counter; //(sitting_counter/3600.00);
                float posturedatavalue = (float) badposture_counter; // (badposture_counter/3600.00);
                entries.add(new BarEntry(date_counter, datavalue));
                postureentries.add(new BarEntry(date_counter, posturedatavalue));
                xAxisLabel.add(loadCells.get(i).getTime_point().substring(0, 8));
            }

        }

        BarDataSet ds = new BarDataSet(entries, "Good posture (seconds)");
        ds.setColors(Color.rgb(100, 164, 156));
        sets.add(ds);

        BarDataSet postureds = new BarDataSet(postureentries, "Bad posture (seconds)");
        postureds.setColors(Color.rgb(245, 136, 0));
        sets.add(postureds);

        BarData d = new BarData(sets);
        return d;
    }




}