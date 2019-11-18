package com.iser.isdotgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

public class MultiPlayingActivity extends AppCompatActivity {
    private DotBoxGame dotBoxGame;
    private Helper helper;
    private int gameSessionId;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_playing);

        helper = new Helper(getApplicationContext());
        this.gameSessionId = (int)(((ObjectWrapperForBinder)getIntent().getExtras().getBinder("gameSessionId")).getData());

        dotBoxGame = new DotBoxGame(this, false, this.gameSessionId, this.helper);
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.addView(dotBoxGame);
        linearLayout.invalidate();

        dotBoxGame.setGameSessionId(this.gameSessionId);
        dotBoxGame.GameIsStarted();
    }
}
