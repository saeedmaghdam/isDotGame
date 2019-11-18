package com.iser.isdotgame;

import android.content.Context;
import android.provider.Settings.Secure;

public class Helper {
    private Context context;

    public Helper(Context context){
        this.context = context;
    }

    public String getUserUniqueId(){
        String userUniqueId = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);

        return userUniqueId;
    }

    public String getUsername(){
        return "";
    }

    public String getHubUrl(){
//        return "http://192.168.0.115:54343/mainhub";
        return "http://192.168.1.253:54343/mainhub";
    }
}
