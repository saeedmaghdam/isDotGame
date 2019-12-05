package com.iser.isdotgame;

import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Callable;

public class BaseActivity extends AppCompatActivity {
    protected Handler handler;
    private String userViewId;
    private String name;
    private String phone;
    private String avatar;
    private String coins;
    private String rate;
    private Callable<Void> updateMethod;

    public void setUserViewId(String userViewId) { this.userViewId = userViewId; }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getUserViewId() {
        return this.userViewId;
    }

    public String getName() {
        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getAvatar() { return this.avatar; }

    public String getCoins() { return this.coins; }

    public String getRate() { return this.rate; }

    public void ackMessage(){
        Message message = new Message();
        message.what = 0;
        handler.sendMessage(message);
    }

    public void setUpdateMethod(Callable<Void> updateMethod){
        this.updateMethod = updateMethod;
    }

    public void updateMethod(){
        try {
            this.updateMethod.call();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
