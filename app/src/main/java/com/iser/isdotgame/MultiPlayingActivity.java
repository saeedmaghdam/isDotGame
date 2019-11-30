package com.iser.isdotgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.microsoft.signalr.HubConnection;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MultiPlayingActivity extends AppCompatActivity {
    private DotBoxGame dotBoxGame;
    private Helper helper;
    private String gameSessionViewId;
    LinearLayout linearLayout;
    HubConnection hubConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_playing);

//        helper = new Helper(getApplicationContext());
        helper = new Helper(this);
        hubConnection = (HubConnection)(((ObjectWrapperForBinder)getIntent().getExtras().getBinder("hubConnection")).getData());
        this.gameSessionViewId = (String)(((ObjectWrapperForBinder)getIntent().getExtras().getBinder("gameSessionViewId")).getData());

        dotBoxGame = new DotBoxGame(this, false, this.gameSessionViewId, this.helper, hubConnection);
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.addView(dotBoxGame);
        linearLayout.invalidate();

        Button btnCharge = findViewById(R.id.btnChargeAccount);
        btnCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle bundle = new Bundle();
                bundle.putBinder("gameSessionViewId", new ObjectWrapperForBinder(-1));
                Intent i = new Intent(getApplicationContext(), ChargeActivity.class).putExtras(bundle);
                startActivity(i);
            }
        });

        dotBoxGame.GameIsStarted();
    }

    private void ChangeTurn(String player){
        View player1Line = findViewById(R.id.player1Line);
        View player2Line = findViewById(R.id.player2Line);

        switch (player.toLowerCase()){
            case "a":
                player1Line.setVisibility(VISIBLE);
                player2Line.setVisibility(INVISIBLE);
                break;
            case "b":
                player1Line.setVisibility(INVISIBLE);
                player2Line.setVisibility(VISIBLE);
                break;
        }
    }
}
