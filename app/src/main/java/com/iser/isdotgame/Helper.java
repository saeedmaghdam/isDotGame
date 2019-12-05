package com.iser.isdotgame;

import android.app.Dialog;
import android.content.Context;
import android.provider.Settings.Secure;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.SupportActionModeWrapper;

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

    public void showMessage(String title, String message)
    {
//        Dialog d = new Dialog(context);
//        d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        d.setContentView(R.layout.dialog);
//        TextView messageTv = d.findViewById(R.id.message);
//        messageTv.setText(message);
//        d.setTitle(title);
//        d.show();
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void showMessage(String message)
    {
        showMessage("توجه", message);
    }

    public String getUsername(){
        return "";
    }

//    private static String baseUrl = "http://192.168.1.253:54343/";
    private static String baseUrl = "http://192.168.0.115:54343/";

    public String getHubUrl(){
//        return "http://poldash.ir/mainhub";
//        return "http://192.168.0.115:54343/mainhub";
        return baseUrl + "mainhub";
    }

    public static String getUploadUrl(){
//        return "http://poldash.ir/mainhub";
//        return "http://192.168.0.115:54343/mainhub";
        return baseUrl + "FileUpload/";
    }
}
