package com.example.dva;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GetData.AsyncResponse {

    private static final String TAG= "MainActivity";
    private Button searchButton;
    private EditText searchField;
    private TextView cityName;
    private URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton=findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        searchField=findViewById(R.id.searchField);
        cityName=findViewById(R.id.cityName);

    }

    @Override
    public void onClick(View v) {
        if(TextUtils.isEmpty(searchField.getText().toString())){
            Toast toast=Toast.makeText(getApplicationContext(), R.string.warning, Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        url = buildUrl(searchField.getText().toString());
        cityName.setText(searchField.getText().toString());
        new GetData(this).execute(url);
        //URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=Samara&lang=ru&appid=c1438b7ab7adcbb5ffee5d716f1141db&units=metric");
    }
    private URL buildUrl (String city){
        String BASE_URL="https://api.openweathermap.org/data/2.5/weather";
        String PARAM_CITY="q";
        String LANG="lang";
        String LANG_VAL="ru";
        String PARAM_APPID="appid";
        String appid_value="c1438b7ab7adcbb5ffee5d716f1141db";
        String UNITS="units";
        String UNITS_VAL="metric";

        Uri builtUri=Uri.parse(BASE_URL).buildUpon().appendQueryParameter(PARAM_CITY,city).appendQueryParameter(LANG,LANG_VAL).appendQueryParameter(PARAM_APPID,appid_value).appendQueryParameter(UNITS,UNITS_VAL).build();
        URL url= null;
        try {
            url=new URL(builtUri.toString());

        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.d(TAG, "buildUrl: "+url);
        return url;
    }
    @Override
    public void processFinish(String output) {
        Log.d(TAG, "processFinish: "+output);
        try {
            JSONObject resultJSON = new JSONObject(output);
            JSONObject weatherPoint = resultJSON.getJSONObject("main");
            JSONObject sys = resultJSON.getJSONObject("sys");
            JSONObject windSpeed = resultJSON.getJSONObject("wind");

            JSONArray weatherNow = resultJSON.getJSONArray("weather");

            TextView description = findViewById(R.id.clouds);
            String descriptionVal = null;
            for (int i = 0; i < weatherNow.length(); i++) {
                JSONObject j = weatherNow.getJSONObject(i);
                descriptionVal = j.getString("description");
            }
            description.setText(descriptionVal);

            TextView temp = findViewById(R.id.temperature);
            temp.setText(weatherPoint.getString("temp"));

            TextView pressure = findViewById(R.id.pressure);
            pressure.setText(weatherPoint.getString("pressure"));

            TextView sunrise = findViewById(R.id.timeSunrise);
            String timeSunrise = sys.getString("sunrise");
            Locale myLocale = new Locale("ru", "RU");
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", myLocale);
            String dateString = formatter.format(new Date(Long.parseLong(timeSunrise) * 1000 + (60 * 60 * 1000)));
            sunrise.setText(dateString);

            TextView sunset = findViewById(R.id.timeSunset);
            String timeSunset = sys.getString("sunset");
            //Locale myLocale=new Locale("ru", "RU");
            //SimpleDateFormat formatter=new SimpleDateFormat("HH:mm:ss", myLocale);
            dateString = formatter.format(new Date(Long.parseLong(timeSunset) * 1000 + (60 * 60 * 1000)));
            sunset.setText(dateString);

            TextView wind = findViewById(R.id.wind);
            wind.setText(windSpeed.getString("speed"));

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
