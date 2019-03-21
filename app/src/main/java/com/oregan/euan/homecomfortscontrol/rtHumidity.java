package com.oregan.euan.homecomfortscontrol;

import android.os.AsyncTask;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class rtHumidity extends AsyncTask <Void, Void, String>{

    private WeakReference<TextView> weatherTextView;
    rtHumidity (TextView tv){
        weatherTextView = new WeakReference<>(tv);
    }

    WeatherAPI api = new WeatherAPI();


    @Override
    protected String doInBackground(Void... voids) {
        String result = api.requestMain("humidity");

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        weatherTextView.get().setText(result);
    }
}