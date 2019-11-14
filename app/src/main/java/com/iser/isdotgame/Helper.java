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
}
