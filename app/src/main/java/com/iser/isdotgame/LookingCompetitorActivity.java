package com.iser.isdotgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

public class LookingCompetitorActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    HubConnection hubConnection;
    Helper helper;
    boolean isConnected = false;
    private String gameSessionViewId;
    DotBoxGame dotBoxGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookingcompetitoractivity);

        Button buttonTest = findViewById(R.id.test);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
                    hubConnection.start();

                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED){
                    // Rise my hand!
                    hubConnection.send("Test", helper.getUserUniqueId());
                }
            }
        });

        helper = new Helper(this);

//        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
//        linearLayout = findViewById(R.id.linearLayout);
//        LinearLayout gif = findViewById(R.id.gif);

//        hubConnection = HubConnectionBuilder.create(helper.getHubUrl()).build();
        hubConnection = (HubConnection)(((ObjectWrapperForBinder)getIntent().getExtras().getBinder("hubConnection")).getData());
//        final HubConnection hubConnection = (HubConnection)(((ObjectWrapperForBinder)getIntent().getExtras().getBinder("hubConnection")).getData());

        hubConnection.on("StartTheGame", (gameSessionId) -> {
            try {
                this.gameSessionViewId = gameSessionId.toString();
            }
            catch (Exception ex){
                this.gameSessionViewId = "";
            }
//            dotBoxGame = new DotBoxGame(this, this.hubConnection, false, this.gameSessionViewId, this.helper);
//            dotBoxGame.setGameSessionViewId(this.gameSessionViewId);
//            dotBoxGame.GameIsStarted();
//
////            mainLayout.removeView(gif);
//            gif.setVisibility(View.GONE);
//            gif.invalidate();
//
//            linearLayout = findViewById(R.id.linearLayout);
//            linearLayout.addView(dotBoxGame);
//
//            mainLayout.invalidate();
//            linearLayout.invalidate();
            // Close current activity and open the relative activity

            //hubConnection.stop().blockingAwait();
            final Bundle bundle = new Bundle();
            bundle.putBinder("gameSessionViewId", new ObjectWrapperForBinder(this.gameSessionViewId));
            bundle.putBinder("hubConnection", new ObjectWrapperForBinder(hubConnection));
            Intent i = new Intent(getApplicationContext(), MultiPlayingActivity.class).putExtras(bundle);
            startActivity(i);
        }, String.class);

        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            try {
                hubConnection.start().blockingAwait();
            } catch (Exception ex){
                helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
            }
        }

        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            // Rise my hand!
            try {
                hubConnection.send("RiseMyHand", helper.getUserUniqueId(), helper.getUsername());
            } catch (Exception ex){
                helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
            }
        }

//        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
//            buttonTest.setText("Trying to connect ...");
//            hubConnection.start().doFinally(new Action() {
//                @Override
//                public void run() throws Exception {
//                    buttonTest.setText("Connected!");
//                    linearLayout.invalidate();
//
//                    if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED){
//                        // Rise my hand!
//                        hubConnection.send("RiseMyHand", helper.getUserUniqueId(), helper.getUsername());
////            hubConnection.send("Test", helper.getUserUniqueId());
//                    }
//                }
//            });
//        }

//        Handler handler = new Handler();
//        Runnable r=new Runnable() {
//            public void run() {
//                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
//                    isConnected = true;
//                else
//                    hubConnection.start();
//            }
//        };
//
//        while (!isConnected)
//        {
//            handler.postDelayed(r, 1000);
//        }

//        buttonTest.setText("Connected!");
//        linearLayout.invalidate();
//
//        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED){
//            // Rise my hand!
//            hubConnection.send("RiseMyHand", helper.getUserUniqueId(), helper.getUsername());
////            hubConnection.send("Test", helper.getUserUniqueId());
//        }

//        linearLayout = findViewById(R.id.linearLayout);
//        MyBoard myBoard = new MyBoard(this);
//        linearLayout.addView(myBoard);
    }
}
