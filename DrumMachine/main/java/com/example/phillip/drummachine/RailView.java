package com.example.phillip.drummachine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.util.AttributeSet;

/**
 * Created by Phillip on 12/3/2017.
 */

public class RailView extends View
{
    private final int BUTTON_SIZE=50;
    private Paint paint;
    private int subdivisions;

    private int workingWidth;
    private Rect container;
    private Rect buttonPlace;

    private BitmapFactory bitF;
    private Bitmap activeButtonPic;
    private Bitmap hitButtonPic;
    private Bitmap background;
    private int width;
    private int minY;
    private int maxY;
    private int rangeY;
    private int xStart;


    private AudioRail audioRail; //Model for controlling data about this Rail View
    public RailView(Context context, int newWidth, int newMinY, int newMaxY, int newRange) //May Need to change later to get approriate window size
    {
        super(context);

        width = newWidth;
        minY=newMinY;
        maxY = newMaxY;
        rangeY = newRange;
        workingWidth = (int)(width*.9);

        audioRail = new AudioRail(width,maxY, workingWidth);//The Data model

        bitF = new BitmapFactory();
        activeButtonPic = bitF.decodeResource(getResources(), R.drawable.innactive_button); //Temp image holder
        hitButtonPic = bitF.decodeResource(getResources(),R.drawable.innactive_button_hit);
        background = bitF.decodeResource(getResources(),R.drawable.rail_back);
        paint = new Paint();

        //subdivisions = audioRail.getSubDivision();
        xStart = (width-workingWidth)/2;

      //Temp Rects
        container = new Rect(0,0,width,rangeY);
        buttonPlace = new Rect(((width-workingWidth)/2)-BUTTON_SIZE, (maxY/2)-BUTTON_SIZE,((width-workingWidth)/2)+BUTTON_SIZE,(maxY/2)+BUTTON_SIZE );
    }


    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        canvas.drawBitmap(background,null,container,paint);

        drawVerticalGuides(canvas, paint);
        drawActiveTriggers(canvas, paint);


    }

    private void drawVerticalGuides(Canvas canvas, Paint paint)
    {
        int spacing = audioRail.getSpace();
        for(int i = 0; i < audioRail.getSubDivision(); i++)
        {
            canvas.drawLine(xStart+(spacing*i),0,xStart+(spacing*i),maxY,paint);
        }
    }

    public AudioRail getAudioRail() {
        return audioRail;
    }

    private void drawActiveTriggers(Canvas canvas, Paint paint)
    {
        int maxBeats = audioRail.getMAX_BEAT_DIV();

        int lowestSpacing = audioRail.getLowestSpacing();
        for(int i=0; i<maxBeats; i++)
        {

            if(audioRail.isTriggered(i))
            {
                if(audioRail.getAnimationTime(i)>0) {
                    canvas.drawBitmap(hitButtonPic, null, buttonPlace, paint);
                    audioRail.iterateHitAnimation(i);
                }
                else
                    canvas.drawBitmap(activeButtonPic,null,buttonPlace,paint);
            }
            buttonPlace.offset(lowestSpacing,0);
        }
        buttonPlace.set(((width-workingWidth)/2)-BUTTON_SIZE, (maxY/2)-BUTTON_SIZE,((width-workingWidth)/2)+BUTTON_SIZE,(maxY/2)+BUTTON_SIZE ); //reset buttonPlace after all said and done
    }

}
