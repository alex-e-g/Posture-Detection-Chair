package com.example.posturalassessment.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class CustomView extends View {

    private static int SQUARE_SIZE = 200;
    private static int SQUARE_length = 730;
    private static int SQUARE_height = 640;



    private Rect mRectCanvas;
    private Paint mPaintCanvas;

    private Rect mSquareCell1;
    private Paint mPaintCell1;

    private Rect mSquareCell2;
    private Paint mPaintCell2;

    private Rect mSquareCell3;
    private Paint mPaintCell3;

    private int C1_Color = Color.GREEN;
    private int C2_Color = Color.GREEN;
    private int C3_Color = Color.GREEN;


    public CustomView(Context context) {
        super(context);

        init(null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }


    public void init(@Nullable AttributeSet set){
        mRectCanvas = new Rect();
        mPaintCanvas = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSquareCell1 = new Rect();
        mPaintCell1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSquareCell2 = new Rect();
        mPaintCell2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSquareCell3 = new Rect();
        mPaintCell3 = new Paint(Paint.ANTI_ALIAS_FLAG);

        /*
        if (set==null)
            return;



        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.CustomView);
        C1_Color = ta.getColor(R.styleable.CustomView_C1_Color, Color.GREEN);
        C2_Color = ta.getColor(R.styleable.CustomView_C2_Color, Color.GREEN);
        C3_Color = ta.getColor(R.styleable.CustomView_C3_Color, Color.GREEN);
        */

        mPaintCell1.setColor(C1_Color);
        mPaintCell2.setColor(C2_Color);
        mPaintCell3.setColor(C3_Color);

        //ta.recycle();
    }


    public void paintcell(int C1_Color, int C2_Color, int C3_Color){
        this.C1_Color = C1_Color;
        this.C2_Color = C2_Color;
        this.C3_Color = C3_Color;
        postInvalidate();


    }

    public void setC1_Color(int c1_Color) {
        C1_Color = c1_Color;
        invalidate();
    }

    public void setC2_Color(int c2_Color) {
        C2_Color = c2_Color;
    }

    public void setC3_Color(int c3_Color) {
        C3_Color = c3_Color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.rgb(245,136,0));

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        // Draw Chair Border
        SQUARE_height = h - 40;
        SQUARE_length = w - 40;

        mRectCanvas.left = 20;
        mRectCanvas.top = 20;
        mRectCanvas.right = mRectCanvas.left + SQUARE_length;
        mRectCanvas.bottom = mRectCanvas.top + SQUARE_height;

        mPaintCanvas.setColor(Color.rgb(255, 251,240));

        canvas.drawRect(mRectCanvas, mPaintCanvas);

        mPaintCell1.setColor(C1_Color);
        mPaintCell2.setColor(C2_Color);
        mPaintCell3.setColor(C3_Color);

        // Draw LoadCell 1
        mSquareCell1.left = 60;
        mSquareCell1.top = 60;
        mSquareCell1.right = mSquareCell1.left + SQUARE_SIZE;
        mSquareCell1.bottom = mSquareCell1.top + SQUARE_SIZE;


        canvas.drawRect(mSquareCell1, mPaintCell1);

        // Draw LoadCell 2
        mSquareCell2.left = w - SQUARE_SIZE - 60;
        mSquareCell2.top = 60;
        mSquareCell2.right = mSquareCell2.left + SQUARE_SIZE;
        mSquareCell2.bottom = mSquareCell2.top + SQUARE_SIZE;


        canvas.drawRect(mSquareCell2, mPaintCell2);

        // Draw LoadCell 3
        mSquareCell3.left = w/2 - SQUARE_SIZE/2;
        mSquareCell3.top = h - 60 - SQUARE_SIZE;
        mSquareCell3.right = mSquareCell3.left + SQUARE_SIZE;
        mSquareCell3.bottom = mSquareCell3.top + SQUARE_SIZE;


        canvas.drawRect(mSquareCell3, mPaintCell3);






    }
}
