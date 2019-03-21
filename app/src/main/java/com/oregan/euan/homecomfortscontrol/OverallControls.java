package com.oregan.euan.homecomfortscontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class OverallControls extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall_controls);
    }

    public void launchHeating(View view) {
        Intent intent = new Intent(this, HeatingActivity.class);
        startActivity(intent);
    }

    public void launchLights(View view){
        Intent intent = new Intent(this, LightsActivity.class);
        startActivity(intent);
    }
}
