package com.oregan.euan.homecomfortscontrol;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LightsActivity extends AppCompatActivity {

    private TextView lightsState;
    private Switch lightsSwitch;
    private Switch lightsAutomationSwitch;

    private EditText startTime;
    private EditText endTime;

    private String onText = "On";
    private String offText =  "Off";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db = database.getReference("house");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights);
        FirebaseApp.initializeApp(this);

        lightsSwitch = findViewById(R.id.lightsSwitch);
        lightsAutomationSwitch = findViewById(R.id.lightsAutomationSwitch);
        lightsState = findViewById(R.id.lightsState);

        startTime = findViewById(R.id.startTimeEditText);
        endTime = findViewById(R.id.endTimeEditText);

        initSetup();
        switchSetup();
    }

    protected void initSetup(){
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean state = dataSnapshot.child("lights").child("active")
                        .getValue(boolean.class);
                if(state){
                    lightsState.setText(onText);
                    lightsSwitch.setChecked(true);
                }
                else if(!state){
                    lightsState.setText(offText);
                    lightsSwitch.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Failed to connect to DB",databaseError.getDetails());
            }
        });
    }

    protected void switchSetup(){
        lightsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    db.child("lights").child("active").setValue(true);
                    lightsState.setText(onText);
                }

                else{
                    db.child("lights").child("active").setValue(false);
                    lightsState.setText(offText);
                }
            }
        });

        lightsAutomationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    db.child("lights").child("automated").setValue(true);
                }
                else{
                    db.child("lights").child("automated").setValue(false);
                }
            }
        });
    }

    public void updateTimes(View view) {
        String startText = startTime.getText().toString();
        String endText = endTime.getText().toString();

        db.child("lights").child("start").setValue(startText);
        db.child("lights").child("end").setValue(endText);

        Toast.makeText(this, "Times Updated", Toast.LENGTH_LONG).show();
    }
}
