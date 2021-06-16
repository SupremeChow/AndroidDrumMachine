package com.example.phillip.drummachine;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.app.Fragment;

import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.Timer;

public class MainEditor extends Fragment implements View.OnTouchListener{

    private PlayView playEditor; //   Used for play animation
    private RailView hiHatView;
    private RailView snareView;
    private RailView kickView;
    private View returnView;
    private int width; //The given allocated width, which should be the parent. Will pass to view
    private int height; //The given allocated TOTAL height of the fragment. Will divide by three and pass to views
    private int deltaTime;// Use to set up speed to compare against BPM

    public MainEditor() {
        // Required empty public constructor
    }

    @Override
    //Use onCreate so that MainActivity can pass arguments of Fragment Size to work with
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        width = getArguments().getInt("newWidth");
        height = getArguments().getInt("newHeight");
        deltaTime=getArguments().getInt("newDeltaTime");

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        returnView = inflater.inflate(R.layout.fragment_main__editor, container, false);

        return returnView;//returnView;//layout;

    }

    @Override
    public void onStart() {
        super.onStart();

        setUpFragmentGUI();
        addPlayView();
        Timer drumTimer = new Timer();
        drumTimer.schedule(new DrumTimerTask(hiHatView,snareView,kickView,playEditor),0, deltaTime);

    }

    public void setUpFragmentGUI()
    {
        int minY;
        int maxY;
        int range = height/3;

        minY=0;
        maxY=minY+range;

        View fragView = getView();

        if(hiHatView == null)
        {
            LinearLayout hiHat =(LinearLayout) fragView.findViewById(R.id.hiHatRailLayout);
            hiHatView = new RailView(getActivity(), width,minY,maxY,range);
            hiHatView.setOnTouchListener(this);
            hiHat.addView(hiHatView);
        }

        if(snareView == null)
        {
            LinearLayout snare =(LinearLayout) fragView.findViewById(R.id.snareRailLayout);
            snareView = new RailView(getActivity(), width,minY,maxY,range);
            snareView.setOnTouchListener(this);
            snare.addView(snareView);
        }


        if(kickView == null)
        {
            LinearLayout kick =(LinearLayout) fragView.findViewById(R.id.kickRailLayout);
            kickView = new RailView(getActivity() , width,minY,maxY,range);
            kickView.setOnTouchListener(this);
            kick.addView(kickView);
        }


    }

    public void addPlayView()
    {
        if (playEditor == null)
        {
            FrameLayout theFrame = (FrameLayout) getView().findViewById(R.id.frameEditor);
            playEditor = new PlayView(getActivity(), width,0,height,deltaTime);
            theFrame.addView(playEditor);
        }


    }

    public void togglePlay(int bpm)
    {
        playEditor.getPlayRail().togglePlay();
        if(playEditor.getPlayRail().isPlaying()) {
            setBPM(bpm);
            playEditor.setVisibility(View.VISIBLE);
        }

        else
            playEditor.setVisibility(View.INVISIBLE);
    }


    public PlayView getPlayEditor() {
        return playEditor;
    }

    public RailView getHiHatView() {
        return hiHatView;
    }

    public RailView getSnareView() {
        return snareView;
    }

    public RailView getKickView() {
        return kickView;
    }
    public int getDeltaTime()
    {
        return deltaTime;
    }


    //Touch method will only work if not playing, so need to disable/remove playView temporarily to allow accurate touch
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(!playEditor.getPlayRail().isPlaying()) //Will check to make sure not playing to avoid issues
            {
                ((RailView)v).getAudioRail().toggleTrigger((int)(event.getX()),(int)(event.getY()));
            }
        }
        return true;
    }

    public void setSubDivision(int subdivision)
    {
        if(!playEditor.getPlayRail().isPlaying()) {
            hiHatView.getAudioRail().setSubDivision(subdivision);
            hiHatView.postInvalidate();
            snareView.getAudioRail().setSubDivision(subdivision);
            snareView.postInvalidate();
            kickView.getAudioRail().setSubDivision(subdivision);
            kickView.postInvalidate();
        }
    }

    public void setBPM(int newBPM)
    {
        playEditor.getPlayRail().setBPM(newBPM);


        /*   The below would have been used if TimerTask Refresh rate can be updated based on bpm. This would have provided better accuracy and timing
        Implement at a Furture date */

        //int recomendedPlayRate = playEditor.getPlayRail().getRecomendedPlayRate();
        //System.out.println("Recomended Playrate Test for bpm: " + newBPM + ", is: " + recomendedPlayRate);



        hiHatView.getAudioRail().setPlayTolerance(newBPM);
        snareView.getAudioRail().setPlayTolerance(newBPM);
        kickView.getAudioRail().setPlayTolerance(newBPM);
    }
    public int getBPM()
    {
        return playEditor.getPlayRail().getBPM();
    }
    public void clearTracks()
    {
        hiHatView.getAudioRail().resetTracks();
        hiHatView.postInvalidate();
        snareView.getAudioRail().resetTracks();
        snareView.postInvalidate();
        kickView.getAudioRail().resetTracks();
        kickView.postInvalidate();
    }

}
