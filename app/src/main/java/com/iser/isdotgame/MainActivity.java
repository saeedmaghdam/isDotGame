package com.iser.isdotgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public class MainActivity extends AppCompatActivity {
//    LinearLayout linearLayout;

    HubConnection hubConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hubConnection = HubConnectionBuilder.create("http://192.168.1.253:54343/mainhub").build();
//        hubConnection = HubConnectionBuilder.create("http://192.168.0.115:54343/mainhub").build();

        Button singlePlaying = (Button)findViewById(R.id.btnSinglePlayer);
        singlePlaying.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), SinglePlayingActivity.class);
                startActivity(i);
            }
        });

        Button onlinePlaying = (Button)findViewById(R.id.btnStartMultiPlayer);
        onlinePlaying.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Bundle bundle = new Bundle();
                bundle.putBinder("hubConnection", new ObjectWrapperForBinder(hubConnection));
                Intent i = new Intent(getApplicationContext(), LookingCompetitorActivity.class).putExtras(bundle);

//                if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
//                    hubConnection.start();
//
//                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
//                    hubConnection.send("SayImHere");
//                }

                startActivity(i);
//                if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
//                    hubConnection.start().blockingAwait();
            }
        });


//        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
//            hubConnection.start();





//
//        hubConnection.on("AckResponse", () -> {
//            Button btn = (Button)findViewById(R.id.onlinePlaying);
//            btn.setText("Ok!");
//        });
//
//        Button connBtn = (Button)findViewById(R.id.con);
//        connBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (connBtn.getText().toString().toLowerCase().equals("start")){
//                    if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
//                        hubConnection.start();
//                    connBtn.setText("Stop");
//                } else if (connBtn.getText().toString().toLowerCase().equals("stop")){
//                    if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
//                        hubConnection.stop();
//                    connBtn.setText("Start");
//                }
//            }
//        });
//
//
//        Button btn = (Button)findViewById(R.id.onlinePlaying);
//        btn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
//                    hubConnection.send("Ack", "isDotGame");
//                }
//                else{
//                    System.out.println("Connection is not active ===========================");
//                }
//            }
//        });
//
//        linearLayout = findViewById(R.id.linearLayout);
//        MyBoard myBoard = new MyBoard(this);
//        linearLayout.addView(myBoard);
    }
}