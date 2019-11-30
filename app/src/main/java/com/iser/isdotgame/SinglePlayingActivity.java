package com.iser.isdotgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.microsoft.signalr.HubConnection;

public class SinglePlayingActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    Helper helper;
    HubConnection hubConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_play);

        helper = new Helper(getApplicationContext());
        hubConnection = (HubConnection)(((ObjectWrapperForBinder)getIntent().getExtras().getBinder("hubConnection")).getData());

        linearLayout = findViewById(R.id.linearLayout);
        linearLayout = findViewById(R.id.linearLayout);
        DotBoxGame dotBoxGame = new DotBoxGame(this, true, "", helper, hubConnection);
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
}
