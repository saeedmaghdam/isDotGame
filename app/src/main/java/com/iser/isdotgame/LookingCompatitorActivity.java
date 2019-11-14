package com.iser.isdotgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

public class LookingCompatitorActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    HubConnection hubConnection;
    Helper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        helper = new Helper(getApplicationContext());

        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
        linearLayout = findViewById(R.id.linearLayout);
        LinearLayout gif = findViewById(R.id.gif);

        linearLayout = findViewById(R.id.linearLayout);
        DotBoxGame dotBoxGame = new DotBoxGame(this);
        linearLayout.addView(dotBoxGame);

        hubConnection = HubConnectionBuilder.create("http://192.168.1.253:54343/mainhub").build();
        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
            hubConnection.start();

        hubConnection.on("LetsPlayTogether", () -> {

        });

//        final HubConnection hubConnection = (HubConnection)(((ObjectWrapperForBinder)getIntent().getExtras().getBinder("hubConnection")).getData());
        hubConnection.on("ImHere", () -> {
            gif.setVisibility(View.GONE);
            gif.invalidate();
            mainLayout.invalidate();
            linearLayout.invalidate();

//            linearLayout.setVisibility(View.VISIBLE);
//            linearLayout.invalidate();
        });

        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED){
            // Rise your hand!
            hubConnection.send("LetMeJoinOnlineGames", helper.getUserUniqueId());
        }

//        linearLayout = findViewById(R.id.linearLayout);
//        MyBoard myBoard = new MyBoard(this);
//        linearLayout.addView(myBoard);
    }
}
