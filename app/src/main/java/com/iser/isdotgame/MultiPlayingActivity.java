package com.iser.isdotgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.percentlayout.widget.PercentRelativeLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.microsoft.signalr.HubConnection;

import java.util.concurrent.Callable;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MultiPlayingActivity extends BaseActivity {
    private DotBoxGame dotBoxGame;
    private Helper helper;
    private String gameSessionViewId;
    LinearLayout linearLayout;
    HubConnection hubConnection;
    SignalRService service;
    Handler handler;
    String compatitorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_playing);

        startService(new Intent(this, SoundService.class));

//        helper = new Helper(getApplicationContext());
        helper = new Helper(this);
//        hubConnection = (HubConnection)(((ObjectWrapperForBinder)getIntent().getExtras().getBinder("hubConnection")).getData());
        this.gameSessionViewId = (String)(((ObjectWrapperForBinder)getIntent().getExtras().getBinder("gameSessionViewId")).getData());
        this.compatitorName = (String)(((ObjectWrapperForBinder)getIntent().getExtras().getBinder("compatitorName")).getData());
        service = new SignalRService(this, handler);
        super.setUpdateMethod(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                CheckFields();
                return null;
            }
        });

//        hubConnection = HubConnectionBuilder.create(this.helper.getHubUrl()).build();
        hubConnection = service.getHubConnection();

        dotBoxGame = new DotBoxGame(this, false, this.gameSessionViewId, this.helper, hubConnection, handler);
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

    private void CheckFields(){
        TextView player1 = findViewById(R.id.myName);
        TextView player2 = findViewById(R.id.compatitorName);
        if (!TextUtils.isEmpty(getName())){
//            player1.setText(getName());
            player1.setText("شما");
        }
        else {
            player1.setText("شما");
        }
        if (!TextUtils.isEmpty(this.compatitorName)) {
            player2.setText(this.compatitorName);
        }
        else {
            player2.setText("رقیب");
        }

//        PercentRelativeLayout playersInfoLayout = findViewById(R.id.playersInfoLayout);
//        playersInfoLayout.invalidate();
    }

//    private void ChangeTurn(String player){
////        runOnUiThread(new Runnable() {
////            @Override
////            public void run() {
////
////            }
////        });
//
//        View player1Line = findViewById(R.id.player1Line);
//        View player2Line = findViewById(R.id.player2Line);
//
//        switch (player.toLowerCase()){
//            case "a":
//                player1Line.setVisibility(VISIBLE);
//                player2Line.setVisibility(INVISIBLE);
//                break;
//            case "b":
//                player1Line.setVisibility(INVISIBLE);
//                player2Line.setVisibility(VISIBLE);
//                break;
//        }
//    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        //stop service and stop music
        stopService(new Intent(this, SoundService.class));
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        stopService(new Intent(this, SoundService.class));
        super.onPause();
    }

    @Override
    protected void onResume(){
        startService(new Intent(this, SoundService.class));
        super.onResume();
    }
}
