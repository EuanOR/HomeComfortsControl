package com.oregan.euan.homecomfortscontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class RoomsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);
    }

    public void launchKitchen(View view) {
        Intent intent = new Intent(this, KitchenActivity.class);
        startActivity(intent);
    }

    public void launchBedroom(View view) {
        Intent intent = new Intent(this, BedroomActivity.class);
        startActivity(intent);
    }
}
