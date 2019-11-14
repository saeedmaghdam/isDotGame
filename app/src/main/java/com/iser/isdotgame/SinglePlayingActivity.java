package com.iser.isdotgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class SinglePlayingActivity extends AppCompatActivity {
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_play);

        linearLayout = findViewById(R.id.linearLayout);
        linearLayout = findViewById(R.id.linearLayout);
        DotBoxGame dotBoxGame = new DotBoxGame(this);
        linearLayout.addView(dotBoxGame);
    }
}
