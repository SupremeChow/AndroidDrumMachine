package com.example.phillip.drummachine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by Phillip on 12/9/2017.
 */

public class PlayView extends View
{
    private final int BUTTON_SIZE=50;
    private Paint paint;

    private PlayRail myRail;

    private int workingWidth;
    private Rect container;
    private Rect buttonPlace;
    private  Rect playLineRect;

    private BitmapFactory bitF;
    private Bitmap playbarPic;
    private int width;
    private int minY;
    private int maxY;
    private int rangeY;
    private int xStart;


    public PlayView(Context context, int newWidth, int newMinY, int newMaxY, int newDeltaTime) //May Need to change later to get approriate window size
    {
        super(context);

        width = newWidth;
        minY=newMinY;
        maxY = newMaxY;
        workingWidth = (int)(width*.9);
        myRail = new PlayRail(width,maxY, workingWidth,newDeltaTime);//The Data model



        bitF = new BitmapFactory();
        playbarPic = bitF.decodeResource(getResources(), R.drawable.play_bar_target); //Temp image holder
        paint = new Paint();


        xStart = myRail.getxPosition();//(width-workingWidth)/2;



        //Temp Rects////
        container = new Rect(0,0,width,rangeY);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(myRail.isPlaying())
        {
            paint.setColor(Color.GRAY);//TEMPORARY, may change later
            paint.setStyle(Paint.Style.STROKE);
            //canvas.drawRect(myRail.getPlayRect(),paint);//MAY CHANGE LATER TO ACTUAL IMAGE BITMAP
            canvas.drawBitmap(playbarPic,null,myRail.getPlayRect(),paint);
        }
    }
    public PlayRail getPlayRail() {
        return myRail;
    }
}
