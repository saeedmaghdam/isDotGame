package com.iser.isdotgame;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

public class SignalRService {
    private Context context;
    private BaseActivity activity;
    private Handler mHandler;
    private HubConnection hubConnection;
    private Helper helper;

    public SignalRService(Context context, Handler mHandler){
        this.context = context;
        this.activity = (BaseActivity)context;
        this.mHandler = mHandler;

        Init();
    }

    public void Init(){
        helper = new Helper(context);
        hubConnection = HubConnectionBuilder.create(this.helper.getHubUrl()).build();

        hubConnection.on("Ack", (userViewId, name, phone, avatar, coins, rate) -> {
            activity.setUserViewId(userViewId);
            activity.setName(name);
            activity.setPhone(phone);
            activity.setAvatar(avatar);
            activity.updateMethod();
            activity.setCoins(coins);
            activity.setRate(rate);
        }, String.class, String.class, String.class, String.class, String.class, String.class);

        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            try {
                hubConnection.start().blockingAwait();

                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                    try {
                        hubConnection.send("Ack", helper.getUserUniqueId(), helper.getUsername());
                    } catch (Exception ex) {
                        helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                    }
                }
            } catch (Exception ex) {
                helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
            }
        }
    }

    public HubConnection getHubConnection(){
        return this.hubConnection;
    }

//    public Handler getHandler(){
//        return this.mHandler;
//    }
}
