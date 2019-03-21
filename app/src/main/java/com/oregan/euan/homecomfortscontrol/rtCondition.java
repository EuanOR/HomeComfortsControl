package com.oregan.euan.homecomfortscontrol;

import android.os.AsyncTask;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class rtCondition extends AsyncTask<Void, Void, String> {

    private WeakReference<TextView> weatherTextView;

    rtCondition(TextView tv) {
        weatherTextView = new WeakReference<>(tv);
    }

    WeatherAPI api = new WeatherAPI();


    @Override
    protected String doInBackground(Void... voids) {
        String result = api.requestWeather("Main");

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        weatherTextView.get().setText(result);
    }
}