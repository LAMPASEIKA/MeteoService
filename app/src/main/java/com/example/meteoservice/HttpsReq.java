package com.example.meteoservice;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class HttpsReq implements Runnable{

    static final String API_REQUEST = "https://api.weatherapi.com/v1/current.json";

    static final String KEY = "f9180058d8f64f6a83d133222231612";

    static final String CITY = "Volgograd";

    URL url;

    Handler handler;

    public HttpsReq(android.os.Handler handler) {
        this.handler = handler;
        try {
            url = new URL(API_REQUEST+"?"+"q="+CITY+"&"+"key="+KEY);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
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
