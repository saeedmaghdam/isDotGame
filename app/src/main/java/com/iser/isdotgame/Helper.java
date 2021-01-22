package com.iser.isdotgame;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.SupportActionModeWrapper;

import cn.pedant.SweetAlert.SweetAlertDialog;

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

//        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        SweetAlertDialog messageBox = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .setConfirmText("متوجه شدم")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                });
//                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                        sDialog.dismissWithAnimation();
//                    }
//                })
//        .show();

        messageBox.show();

    }

    public void loading(){
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(R.id.primaryLayout);
        pDialog.setTitleText("لطفا منتظر بمانید ...");
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void showMessage(String message)
    {
        showMessage("توجه", message);
    }

    public String getUsername(){
        return "";
    }

//        private static String baseUrl = "http://192.168.1.253:54343/";
//    private static String baseUrl = "http://poldash.ir/";
//private static String baseUrl = "http://192.168.0.115:54343/";
private static String baseUrl = "http://78.47.58.113:5001/";
//private static String baseUrl = "http://localhost:5000/";
//    private static String baseUrl = "http://192.168.0.115:5000/";

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

    public void Sleep(){
        //SystemClock.sleep(100);
    }
}
