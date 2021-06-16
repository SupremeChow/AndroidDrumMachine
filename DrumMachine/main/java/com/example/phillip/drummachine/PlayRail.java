package com.example.phillip.drummachine;

import android.graphics.Rect;

/**
 * Created by Phillip on 12/9/2017.
 */

public class PlayRail
{
    private final int BUTTON_SIZE=50;
    private int width;
    private int height;
    private int workingWidth;
    private int startX; //used to place back at start
    private int endX; //used to signify end of working rail
    private double deltaX; //Used to determine movement speed based on Tempo
    private int deltaTime; //USE THIS For clock time. Set outside of class. Use in conjunction of deltaX
    private int xPosition;//used to Track where the play bar is if playing
    private boolean isPlaying;
    private Rect playRect; //Hold data of where to Rect is
    private int bpm;

    public PlayRail(int newWidth, int newHeight, int newWorkingWidth, int newDeltaTime)
    {
        deltaTime=newDeltaTime;//Assigned refresh time, set up back at Time Scheduler

        width=newWidth;
        workingWidth=newWorkingWidth;
        startX=(width-workingWidth)/2;
        endX=(workingWidth) + ((width-workingWidth)/2);

        xPosition=startX;
        height=newHeight;
        isPlaying= false;

        deltaX = .9; //TEMPORARY!!! NEED TO SET BASED ON BPM, using double to make as fraction

        playRect = new Rect(xPosition-BUTTON_SIZE,0,xPosition+BUTTON_SIZE,height);
        bpm =120;

    }

    public boolean isPlaying()
    {
        return isPlaying;
    }
    public void togglePlay()
    {
        isPlaying=!isPlaying;
        if(!isPlaying())
            resetPosition();
    }
    public void resetPosition()
    {
        xPosition=startX;

    }
    public void setDeltaX() //Improve Logic later, based on Refresh rate set my TimerTask
    {
        if(bpm>0)
        {
            deltaX= ((workingWidth)/((4.0*60*1000)/bpm)); //Try this to determine speed
        }
    }
    public void setDeltaTime(int newTime)
    {
        if(newTime>0)
        deltaTime=newTime;
    }

    public int getxPosition()
    {
        return xPosition;
    }
    public void movePlay()
    {
        xPosition+=(int)(deltaX*deltaTime);

        if(xPosition > endX) //If at the end, reset. Check and see if it doesn't reset too early
        {

            xPosition=startX;
        }
        updateRect();
    }
    public void updateRect()
    {
        playRect.set(xPosition-BUTTON_SIZE,0,xPosition+BUTTON_SIZE, height);
    }
    public Rect getPlayRect()
    {
        return playRect;
    }

    public void setBPM(int newBPM)
    {
        bpm=newBPM;
        setDeltaX();

    }
    public int getBPM()
    {
        return bpm;
    }

    public int getRecomendedPlayRate()
    {
        int totalTime = (4 * 60000)/bpm;
        return (16*workingWidth)/(totalTime*totalTime);
    }

}
