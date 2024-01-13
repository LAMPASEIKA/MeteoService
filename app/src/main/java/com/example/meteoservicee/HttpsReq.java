package com.example.meteoservicee;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class HttpsReq implements Runnable{

    static final String API_REQUEST = "https://api.weatherapi.com/v1/current.json";

    static final String KEY = "f9180058d8f64f6a83d133222231612";

    URL url;

    Handler handler;

    public HttpsReq(android.os.Handler handler, Intent intent) {
        this.handler = handler;
        try {
            //Intent intent = new Intent("CityForReq");
            String City = intent.getStringExtra("CITY");
            url = new URL(API_REQUEST+"?"+"q="+City+"&"+"key="+KEY);
            Log.d("RRR", String.valueOf(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.connect();
            Scanner in = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();

            while(in.hasNext()){
                response.append(in.nextLine());
            }
            in.close();
            connection.disconnect();

            Message msg = Message.obtain();
            msg.obj = response.toString();
            handler.sendMessage(msg);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
