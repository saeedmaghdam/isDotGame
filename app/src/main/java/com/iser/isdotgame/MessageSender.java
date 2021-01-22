package com.iser.isdotgame;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageSender extends AsyncTask<String, Void, Void> {
    Socket s;
    DataOutputStream dos;
    PrintWriter pw;

    String ip;
    int port;

    public MessageSender(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected Void doInBackground(String... voids){
        String message = voids[0];
        try{
            s = new Socket(ip, port);
            pw = new PrintWriter(s.getOutputStream());
            pw.write(message);
            pw.flush();
            pw.close();
            s.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }

        return null;
    }
}
