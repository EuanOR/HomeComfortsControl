package com.oregan.euan.homecomfortscontrol;

import android.content.Context;
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

public class HeatingActivity extends AppCompatActivity {

    private TextView heatingState;
    private Switch heatingSwitch;
    private Switch heatingAutomationSwitch;
    private EditText threshold;

    private String onText = "On";
    private String offText =  "Off";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db = database.getReference("house");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heating);
        FirebaseApp.initializeApp(this);

        heatingSwitch = findViewById(R.id.heatingSwitch);
        heatingAutomationSwitch = findViewById(R.id.heatingAutomationSwitch);
        heatingState = findViewById(R.id.heatingState);
        threshold = findViewById(R.id.updateThresholdEdit);

        initSetup();
        switchSetup();
    }

    protected void initSetup(){

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean state = dataSnapshot.child("heating").child("active")
                        .getValue(boolean.class);
                if(state){
                    heatingState.setText(onText);
                    heatingSwitch.setChecked(true);
                }
                else{
                    heatingState.setText(offText);
                    heatingSwitch.setChecked(false);
                }

                boolean automatedState = dataSnapshot.child("heating").child("automated")
                        .getValue(boolean.class);
                if(automatedState){
                    heatingAutomationSwitch.setChecked(true);
                }
                else if (!automatedState){
                    heatingAutomationSwitch.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Failed to connect to DB",databaseError.getDetails());
            }
        });
    }

    protected void switchSetup(){
        heatingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    db.child("heating").child("active").setValue(true);
                    heatingState.setText(onText);
                }

                else{
                    db.child("heating").child("active").setValue(false);
                    heatingState.setText(offText);
                }
            }
        });

        heatingAutomationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    db.child("heating").child("automated").setValue(true);
                }
                else{
                    db.child("heating").child("automated").setValue(false);
                }
            }
        });
    }

    public void updateThreshold(View view) {
        String thresholdText = threshold.getText().toString();
        Integer thresholdValue =  Integer.parseInt(thresholdText);
        db.child("heating").child("threshold").setValue(thresholdValue);
        String toastText =  String.format("Threshold Updated: %d",thresholdValue);
        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
    }
}