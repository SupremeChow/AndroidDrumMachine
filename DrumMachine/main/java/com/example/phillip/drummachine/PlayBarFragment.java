package com.example.phillip.drummachine;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.example.phillip.drummachine.R;


public class PlayBarFragment extends Fragment {


    public PlayBarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View returnView = inflater.inflate(R.layout.fragment_play_bar, container, false);
        ToggleButton toggle = (ToggleButton) returnView.findViewById(R.id.playButton);
        Drawable playDraw = getResources().getDrawable(R.drawable.on_toggle);

        //simpleToggleButton.setBackgroundResource(R.drawable.on_toggle);
        toggle.setCompoundDrawablesWithIntrinsicBounds(null,playDraw,null,null);
        return returnView;
    }

    //On click methods, may not be necessary since MainActivity might handle them
    public void togglePlay(View view)
    {
        ((MainActivity)(view.getContext())).togglePlay(view);
    }
    public void toggleDiv(View view)
    {
        ((MainActivity)(view.getContext())).toggleDiv(view);
    }


}
