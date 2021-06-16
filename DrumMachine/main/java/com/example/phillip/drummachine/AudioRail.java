package com.example.phillip.drummachine;

/**
 * Created by Phillip on 12/3/2017.
 */

public class AudioRail
{
    private final int MAX_BEAT_DIV = 16; //Will not actually use 16th notes, but use for 16th and 3rd divisions
    private int subDivision; //used for dividing space bye either quarter, eight, sixteenth. Maybe eventaully based on thirds
    private int beatsPerMeasure;
    private int beatsPerMinute;
    private int width; //Total width allocated to view
    private int workingWidth; //The working width, which will be a fraction of width. Adds padding to width for safe drawing. USE THIS FOR CALCING DIVISION AND PLACEMENT
    private int height; //Allocated height
    private int lowestSpacing; //Lowest spacing between notes. for easily going through every note spacing. Based of MAX_BEAT_DIV
    private int space; //Dynamic spacing, used to determine space between notes based on the active subdivision number.
    private int startX; //Easy access to where to place first note
    private int touchTolerance; //USE THIS TO HANDLE THE TOLERANCE OF TOUCHES SO THAT THEY HIT A TRIGGER HITBOX
    private int playTolerance; //USE THIS TO HANDLE PLAY TOUCHES

    private int nextTargetHit; // use this to quickly check where the next hit will occur
    private int numOn; //used to keep track how many hits are triggered on

    private double totalTime; //in seconds
    private int [][] triggerChart;



    public AudioRail(int newWidth, int newHeight, int newWorkingWidth)
    {
        touchTolerance= 50;// Touch tolerance of 10px for now, May change later depending on tests and maybe subdivisions
        playTolerance=1; //Set to 1 for now, may change depending on tempo
        width=newWidth;
        workingWidth=newWorkingWidth;
        height=newHeight;

        //MAKE THE BELOW ASSIGNABLE EVENTUALLY
        subDivision=2;
        beatsPerMeasure=4;
        beatsPerMinute=120;
        findTotalMeasureTime();

        lowestSpacing = workingWidth/MAX_BEAT_DIV;
        triggerChart = new int [MAX_BEAT_DIV][4];
        startX = (width-workingWidth)/2;
        space = workingWidth/subDivision;

        //Initialize TriggerChart array
        for(int i = 0; i < MAX_BEAT_DIV; i++)
        {
            triggerChart[i][0] = 0;// 0 Indicates off
            triggerChart[i][1] = startX + (i*lowestSpacing); //Indicates the X position of each hit
            triggerChart[i][2] = height/2; //Indicates the Y position. Always half height of given View
            triggerChart[i][3] = 0; //May use later to indicate animation process
        }




        nextTargetHit =-1; //WIll initiate to -1 for now, to indicate there is no next hit || **NOTE** Not really implemented, but use in future
        numOn=0; //Always tart at zero to indicate there are no notes assigned
    }


    public int getSpace(){return space;}
    public int getLowestSpacing(){return lowestSpacing;}
    public int getMAX_BEAT_DIV(){return MAX_BEAT_DIV;}
    public int getSubDivision() {
        return subDivision;
    }
    public int getNextTargetHit(){return nextTargetHit;}
    public int getBeatsPerMeasure() {
        return beatsPerMeasure;
    }
    public void setBeatsPerMeasure(int beatsPerMeasure) {
        this.beatsPerMeasure = beatsPerMeasure;
    }
    public int getBeatsPerMinute() {
        return beatsPerMinute;
    }
    public void setBeatsPerMinute(int beatsPerMinute) {
        this.beatsPerMinute = beatsPerMinute;
    }
    public int getTriggerX(int index)
    {
        return triggerChart[index][1];
    }


    public void setSubDivision(int subDivision) {
        this.subDivision = subDivision;
        updateSpacing();
    }

    private void updateSpacing()
    {
        space = workingWidth/subDivision;
    }



    public void findTotalMeasureTime()
    {
        totalTime =((60*1000)*beatsPerMeasure)/beatsPerMinute;
    }

    public boolean isTriggered(int index)
    {
        if(triggerChart[index][0] == 1)
            return true;
        else
            return false;
    }

    /* May implement later, will use check of point position instead for now
    public void toggleTrigger(int triggerNumber)
    {
        triggerChart[triggerNumber] = !triggerChart[triggerNumber];
    }
    */

    //May change eventually to assume the y position is irrelevant
    public void toggleTrigger(int x, int y)
    {
        int triggerNumber = findTriggerByPoint(x);
        if(triggerNumber != -1 && isClickable(triggerNumber))
        {
            if (triggerChart[triggerNumber][0] == 0) {//Turn on

                triggerChart[triggerNumber][0] = 1;
                numOn++;
            } else {                                    //turn off

                triggerChart[triggerNumber][0] = 0;
                numOn--;
            }
        }

    }

    //use this method to assist in note placement when setting subdivision
    private boolean isClickable(int index)
    {
        boolean clickable = false;
        if(index == 0 || subDivision==MAX_BEAT_DIV || index%(MAX_BEAT_DIV/subDivision) == 0)//case of always allow first beat, or max subdivisions
            clickable=true;
        return clickable;
    }


    public int findTriggerByPoint(int x) //We will assume y doesn't matter ||| MAYBE USE RECURSION FOR BETTER SEARCH || USE THIS FOR TOUCH, NOT PLAY!!!!
    {
        int returnTrigger = -1;
        for(int i = 0; i<MAX_BEAT_DIV; i++)
        {
            if(Math.abs(triggerChart[i][1]-x) <= touchTolerance) //Touchtolerance will be defined, Will edit later for Usability as I test
            {
                returnTrigger=i; break;
            }
        }
        return returnTrigger;
    }

    public int findPlayTriggerByPoint(int x) // SEPARATE FROM FindTriggerByPoint. This will use a different constant used for calculating play hits
    {
        int returnTrigger = -1;
        for(int i = 0; i<MAX_BEAT_DIV; i++)
        {
            if(Math.abs(triggerChart[i][1]-x) <=playTolerance && isTriggered(i)) //playTolerance)  will be defined, Will edit later for Usability as I test
            {
                returnTrigger=i; break;
            }
        }
        return returnTrigger;
    }

    /*Not Implemented, would have used to assist in accurate play hits by predicting next occuring note*/
    public void findNextTargetHit() {
        if (numOn == 1) {
            return; //If only one own, don't bother switching nextTarget
        }
        if (numOn > 0) {
            for (int i = (nextTargetHit + 1) % MAX_BEAT_DIV; i < MAX_BEAT_DIV; i++) {
                if (triggerChart[i][0] != 0) {
                    nextTargetHit = i;
                    break;
                }
            }
            nextTargetHit = MAX_BEAT_DIV - 1;
            findTotalMeasureTime();//Recursive call to check values behind
        }
    }

    //Use for animation purposes, AND to help prevent notes being double triggered. Number assinged could be in the future dynamic based on bpm
    public void setHitAnimation(int index)
    {
        triggerChart[index][3] = 5;
    }

    //Used to assist in preventing double note triggering
    public int getAnimationTime(int index)
    {
        return triggerChart[index][3];
    }

    //Used for hit animation
    public void iterateHitAnimation(int index)
    {
        triggerChart[index][3]--;
    }


    //Not perfect, but an effort to make higher bpms register a hit
    public void setPlayTolerance(int bpm)
    {
        if(bpm< 50)
        {
            playTolerance = 1;
        }
        else if (bpm >=50 && bpm < 115)
        {
            playTolerance = 3;
        }
        else if (bpm >=115 && bpm < 135)
        {
            playTolerance = 4;
        }
        else if (bpm >= 135 && bpm < 180)
        {
            playTolerance = 5;
        }
        else
            playTolerance = 6;
    }

    public boolean isRecentlyHit(int index)
    {
        boolean wasHit = false;
        if(index == -1) {
            return true; //Use this incase invalid index was given
        }
        else
        {
            if(triggerChart[index][3] >0)
                wasHit=true;
            else
                wasHit=false;
        }
        return wasHit;
    }

    public void resetTracks()
    {
        for(int i = 0; i < MAX_BEAT_DIV; i++)
        {
            triggerChart[i][0]=0;
        }
    }

}
