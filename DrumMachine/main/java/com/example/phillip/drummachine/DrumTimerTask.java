package com.example.phillip.drummachine;

import java.util.TimerTask;

/**
 * Created by Phillip on 12/6/2017.
 */

public class DrumTimerTask extends TimerTask
{
    private AudioRail hiRail;
    private AudioRail snareRail;
    private AudioRail kickRail;
    private PlayRail playRail;

    private RailView hiView;
    private RailView snareView;
    private RailView kickView;
    private PlayView playView;

    public DrumTimerTask(RailView hi, RailView snare, RailView kick, PlayView play)
    {
        hiRail = hi.getAudioRail();
        snareRail = snare.getAudioRail();
        kickRail = kick.getAudioRail();
        playRail = play.getPlayRail();

        hiView=hi;
        snareView=snare;
        kickView=kick;
        playView=play;

    }

    @Override
    public void run()
    {
        //Update RailViews in case user added a note
        hiView.postInvalidate();
        snareView.postInvalidate();
        kickView.postInvalidate();

        //Case when the drum machine is playing
        if(playRail.isPlaying()) {


            playView.postInvalidate();
            int playX = playRail.getxPosition();

            int hiTarget = hiRail.findPlayTriggerByPoint(playX);
            if (hiTarget >= 0 && !hiRail.isRecentlyHit(hiTarget)) {

                //draw hit icon
               hiRail.setHitAnimation(hiTarget);
                //play sound
                ((MainActivity) hiView.getContext()).playHiSound();

            }

            int snareTarget = snareRail.findPlayTriggerByPoint(playX);

            if (snareTarget >= 0 && !snareRail.isRecentlyHit(snareTarget)) {

                //draw hit icon
               snareRail.setHitAnimation(snareTarget);
                //play sound
                ((MainActivity) snareView.getContext()).playSnareSound();

            }

            int kickTarget = kickRail.findPlayTriggerByPoint(playX);
            if (kickTarget >= 0 && !kickRail.isRecentlyHit(kickTarget)) {

                //draw hit icon
               kickRail.setHitAnimation(kickTarget);
                //play sound
                ((MainActivity) kickView.getContext()).playKickSound();

            }
            playRail.movePlay();
        }
    }
}
