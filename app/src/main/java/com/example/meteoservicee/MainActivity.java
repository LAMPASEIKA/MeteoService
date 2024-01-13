package com.example.meteoservicee;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextView tempView, windView, cityView;
    EditText ET;
    Button But;

    ImageView Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempView = findViewById(R.id.Temperature);
        windView = findViewById(R.id.Wind);
        cityView = findViewById(R.id.City);
        ET = findViewById(R.id.EtCity);
        But = findViewById(R.id.Button);
        Image = findViewById(R.id.Image);

        if (!isConnected()) {
            tempView.setText("Нет соединения");
            windView.setText("Нет соединения");
            cityView.setText("Нет соединения");
        }

        But.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String City = String.valueOf(ET.getText());
                cityView.setText(City);
                ET.setText("");
                if (isConnected()){
                    registerReceiver(receiver, new IntentFilter("MeteoService"), RECEIVER_EXPORTED);

                    Intent intent = new Intent(MainActivity.this, MeteoService.class);
                    intent.putExtra("CITY", City);
                    startService(intent);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(this, MeteoService.class);
        stopService(intent);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isConnected()){
                Log.d("RESULT", Objects.requireNonNull(intent.getStringExtra("INFO")));
                String str = intent.getStringExtra("INFO");
                try {
                    JSONObject start = new JSONObject(str);
                    JSONObject current = start.getJSONObject("current");
                    JSONObject condition = current.getJSONObject("condition");
                    double temp = current.getDouble("temp_c");
                    double wind = current.getDouble("wind_kph");
                    String image_url = "https://" + condition.getString("icon");
                    windView.setText("Ветер км/ч " + wind);
                    tempView.setText("Температура " + temp);
                    new DownloadImageTask((ImageView) findViewById(R.id.Image))
                            .execute(image_url);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    };

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", Objects.requireNonNull(e.getMessage()));
        }
        return connected;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Ошибка передачи изображения", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
