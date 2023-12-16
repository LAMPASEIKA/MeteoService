package com.example.meteoservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView tempView, windView;
    static final String API_REQUEST = "https://api.weatherapi.com/v1/current.json?key=f9180058d8f64f6a83d133222231612&q=Volgograd&aqi=no";

    static final String KEY = "f9180058d8f64f6a83d133222231612";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempView = findViewById(R.id.Temperature);
        windView = findViewById(R.id.Wind);


        registerReceiver(receiver, new IntentFilter("MeteoService"), RECEIVER_EXPORTED);

        Intent intent = new Intent(this, MeteoService.class);
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(this, MeteoService.class);
        stopService(intent);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("RESULT", intent.getStringExtra("INFO"));
            String str = intent.getStringExtra("INFO");
            try {
                JSONObject start = new JSONObject(str);
                JSONObject current = start.getJSONObject("current");
                double temp = current.getDouble("temp_c");
                double wind = current.getDouble("wind_kph");
                windView.setText("Ветер км/ч "+wind);
                tempView.setText("Температура "+temp);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    };
}