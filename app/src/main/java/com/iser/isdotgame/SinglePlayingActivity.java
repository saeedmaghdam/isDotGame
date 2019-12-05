package com.iser.isdotgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.microsoft.signalr.HubConnection;

import java.util.concurrent.Callable;

import static android.view.View.GONE;

public class SinglePlayingActivity extends BaseActivity {
    LinearLayout linearLayout;
    Helper helper;
    HubConnection hubConnection;
//    SignalRService service;
//    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_play);

        helper = new Helper(getApplicationContext());
//        hubConnection = (HubConnection)(((ObjectWrapperForBinder)getIntent().getExtras().getBinder("hubConnection")).getData());
//        service = new SignalRService(this, handler);
//        super.setUpdateMethod(new Callable<Void>() {
//            @Override
//            public Void call() throws Exception {
//                CheckFields();
//                return null;
//            }
//        });
//
////        hubConnection = HubConnectionBuilder.create(this.helper.getHubUrl()).build();
//        hubConnection = service.getHubConnection();

        linearLayout = findViewById(R.id.linearLayout);
        linearLayout = findViewById(R.id.linearLayout);
        DotBoxGame dotBoxGame = new DotBoxGame(this, true, "", helper, hubConnection, handler);
        linearLayout.addView(dotBoxGame);

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
    }

//    private void CheckFields(){
//        runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                TextView coins = findViewById(R.id.coins);
//                TextView rate = findViewById(R.id.rate);
//
//                if (!TextUtils.isEmpty(getCoins())) {
//                    coins.setText(getCoins());
//                }
//                if (!TextUtils.isEmpty(getRate())) {
//                    rate.setText(getRate());
//                }
//            }
//        });
//    }

    @Override
    protected void onResume() {
        super.onResume();
        this.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

            }
        };
    }
}
