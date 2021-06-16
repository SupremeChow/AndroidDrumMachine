package com.example.phillip.drummachine;

//import android.support.v4.app.FragmentManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.util.Timer;


public class MainActivity extends AppCompatActivity {
    private final int DELTA_TIME =10; //Cosntant used for refresh time, and passed to child classes
    private Point screenPoint;
    //Hold the instance of the Fragments to access and communicate between eachother
    private MainEditor mainEditor;
    private Fragment playFragment;
    private SoundPool pool;
    private int hiSoundId;
    private int snareSoundId;
    private int kickSoundId;
    private final int[] DIV_ARRAY = {2, 4, 8, 16};
    private int divCycle;
    private final int DEFAULT_BPM=120; //Use to initialize BPM
    private final int MAX_BPM = 180; //Set an upper BPM limit to avoid skipping issues. Test around to find optimal number.
    private final int MIN_BPM = 20; //Set up an lower BPM limit to avoid douple plays and such. Test around to find optimal number

    private  Drawable PLAY_DRAW ;
    private  Drawable STOP_DRAW ;
    private  Drawable []DIV_DRAW;


//    @RequiresApi(api = Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);
        divCycle = 0;
        int bpm = DEFAULT_BPM; //
        EditText bpmText = (EditText)findViewById(R.id.bpm);
        bpmText.setText(""+bpm); //Set default BPM value

        Resources res = getResources();
        int statusBarHeight = 0;
        int statusBarId = res.getIdentifier("status_bar_height", "dimen","android" );
        if(statusBarId >0)
            statusBarHeight = res.getDimensionPixelSize(statusBarId);

        screenPoint = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenPoint);

        int width = screenPoint.x;//screen width, obviously
        int height = (int)((double)(screenPoint.y-statusBarHeight) * (7.0/10.0));



        /* Create Main Editor Fragment*/

        FragmentManager fm = getFragmentManager();

        if (fm.findFragmentById(R.id.fragmentMainEdit) == null)
        {
            FragmentTransaction ft = fm.beginTransaction();
            mainEditor = newInstanceMainEditor(width, height); //Will send the width and allocated height|
            ft.add(R.id.fragmentMainEdit, mainEditor);
            ft.commit();
        }



        /* Set up playBar Icons */
        PLAY_DRAW = getResources().getDrawable(R.drawable.on_toggle);;
        STOP_DRAW = getResources().getDrawable(R.drawable.off_toggle);
        DIV_DRAW = new Drawable[]{getResources().getDrawable(R.drawable.half), getResources().getDrawable(R.drawable.quarter), getResources().getDrawable(R.drawable.eighth), getResources().getDrawable(R.drawable.sixteenth)};

        Button divButton = (Button)findViewById(R.id.divButton);
        divButton.setCompoundDrawablesWithIntrinsicBounds(null, DIV_DRAW[divCycle], null, null);



        /*Setup Sound Pool */
        SoundPool.Builder poolBuilder = new SoundPool.Builder();
        poolBuilder.setMaxStreams(16); //Expect at most 3 hits of instruments at one time, But test higher incase
        pool = poolBuilder.build();
        hiSoundId = pool.load(this, R.raw.hihat_fix,1);
        snareSoundId = pool.load(this, R.raw.snare_fix,1);
        kickSoundId = pool.load(this, R.raw.kick_fix,1);


    }

    public Point getScreenPoint()
    {
        return  screenPoint;
    } //Maybe Use another time

    /*
        Use this method to instantiate a MainEditor, and set up Bundle that will be used to pass variable Width and Height
        Using this because overloaded Constructor of Fragments not recommended, since they can occasionally be recreated, leading to loss instance state data
     */
    public MainEditor newInstanceMainEditor(int width, int height)
    {
        EditText bpmText = (EditText)findViewById(R.id.bpm);
        int bpm = Integer.parseInt(bpmText.getText().toString());

        MainEditor fragment = new MainEditor();
        Bundle bundle = new Bundle(2);
        bundle.putInt("newWidth", width);
        bundle.putInt("newHeight", height);
        bundle.putInt("newDeltaTime", DELTA_TIME);
        bundle.putInt("newBPM", bpm);
        System.out.println("Creating Instance, Height is now: " + height);
        fragment.setArguments(bundle);
        return fragment ;
    }

    /*Method for Toggle Button Play is pressed*/
    public void togglePlay(View view)
    {
        EditText bpmText = (EditText)findViewById(R.id.bpm);
        int bpm;
        if(bpmText.getText().toString().equals("")) //case where user leaves the input null, will reset to last bpm
        {
            bpm = mainEditor.getBPM();
            bpmText.setText("" + bpm);
        }
        else
             bpm = Integer.parseInt(bpmText.getText().toString());

        //Check case if BPM is above or below limits that may cause issues
        if(bpm>MAX_BPM)
        {
            bpm =MAX_BPM;
            bpmText.setText(""+MAX_BPM);
        }
        if(bpm<MIN_BPM)
        {
            bpm =MIN_BPM;
            bpmText.setText(""+MIN_BPM);
        }

        ToggleButton simpleToggleButton = (ToggleButton)view; // initiate a toggle button
        boolean toggleState = simpleToggleButton.isChecked();
        if(!toggleState) //if at off state
        {
            simpleToggleButton.setCompoundDrawablesWithIntrinsicBounds(null,PLAY_DRAW,null,null);
            bpmText.setInputType(InputType.TYPE_CLASS_NUMBER); // Makes input type numeral, so it is editable again
        }
        else
        {

            simpleToggleButton.setCompoundDrawablesWithIntrinsicBounds(null,STOP_DRAW,null,null);
            bpmText.setInputType(InputType.TYPE_NULL); // Zero is defualt to none, this will disable editing
        }

        mainEditor.togglePlay(bpm);
        playSilence(); //This may help create a buffer to better play audio

    }

    /*Change the sub division of the measure*/
    public void toggleDiv(View view)
    {
        if(!mainEditor.getPlayEditor().getPlayRail().isPlaying())
        {
            Button simpleButton = (Button) view; // initiate a toggle button
            divCycle = (divCycle + 1) % DIV_ARRAY.length;
            simpleButton.setCompoundDrawablesWithIntrinsicBounds(null, DIV_DRAW[divCycle], null, null);
            mainEditor.setSubDivision(DIV_ARRAY[divCycle]);
        }

    }
    /*Clears tracks on any note input*/
    public void clearTracks(View view)
    {
        if(!mainEditor.getPlayEditor().getPlayRail().isPlaying())
        {
            mainEditor.clearTracks();
        }

    }

    /*Sound pool Methods*/
    public void playHiSound()
    {
        pool.play(hiSoundId, 1.0f,1.0f,1,0,1.0f);
    }
    public void playSnareSound()
    {
        pool.play(snareSoundId, 1.0f,1.0f,1,0,1.0f);
    }
    public void playKickSound()
    {
        pool.play(kickSoundId, 1.0f,1.0f,1,0,1.0f);
    }
    public void playSilence()
    {pool.play(kickSoundId, 0.0f,0.0f,1,0,1.0f);}//Use this to create silence to help reduce delay, or missed hits
}
