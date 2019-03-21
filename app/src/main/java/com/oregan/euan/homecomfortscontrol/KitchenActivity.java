package com.oregan.euan.homecomfortscontrol;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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

public class KitchenActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference kitchendb = database.getReference("house/rooms/kitchen");

    private String onText = "On";
    private String offText = "Off";

    private Integer ovenMax;
    private Integer toasterMax;

    private TextView kettleStateText;
    private Switch kettleSwitch;
    private TextView kettleTemperatureText;

    private TextView ovenStateText;
    private Switch ovenSwitch;
    private EditText ovenEditText;

    private TextView toasterStateText;
    private Switch toasterSwitch;
    private EditText toasterEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);
        FirebaseApp.initializeApp(this);

        kettleStateText = findViewById(R.id.kettleStateText);
        kettleSwitch = findViewById(R.id.kettleSwitch);
        kettleTemperatureText = findViewById(R.id.kettleTemperatureState);

        ovenStateText = findViewById(R.id.ovenStateText);
        ovenSwitch = findViewById(R.id.ovenSwitch);
        ovenEditText = findViewById(R.id.ovenTemperatureEditText);

        toasterStateText = findViewById(R.id.toasterStateText);
        toasterSwitch = findViewById(R.id.toasterSwitch);
        toasterEditText = findViewById(R.id.toasterLevelEditText);

        initSetup();
        switchSetup();
    }

    protected void initSetup(){
        kitchendb.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean kettleState = dataSnapshot.child("kettle").child("active")
                        .getValue(boolean.class);
                if(kettleState){
                    kettleStateText.setText(onText);
                    kettleSwitch.setChecked(true);
                }
                else{
                    kettleStateText.setText(offText);
                    kettleSwitch.setChecked(false);
                }
                Integer kettleTemp = Math.toIntExact(dataSnapshot.child("kettle")
                        .child("temperature").getValue(Long.class));
                Log.d("kettle temp", String.valueOf(kettleTemp));
                kettleTemperatureText.setText(String.valueOf(kettleTemp));

                boolean ovenState = dataSnapshot.child("oven").child("active")
                        .getValue(boolean.class);
                if (ovenState){
                    ovenStateText.setText(onText);
                    ovenSwitch.setChecked(true);
                }
                else{
                    ovenStateText.setText(offText);
                    ovenSwitch.setChecked(false);
                }

                boolean toasterState = dataSnapshot.child("toaster").child("active")
                        .getValue(boolean.class);
                if (toasterState){
                    toasterStateText.setText(onText);
                    toasterSwitch.setChecked(true);
                }
                else {
                    toasterStateText.setText(offText);
                    toasterSwitch.setChecked(false);
                }

                ovenMax = Math.toIntExact(dataSnapshot.child("oven").child("max_temp")
                        .getValue(long.class));
                toasterMax = Math.toIntExact(dataSnapshot.child("toaster").child("max_level")
                        .getValue(long.class));
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase Error:",databaseError.getDetails());
            }
        });


    }
    protected void switchSetup(){
        kettleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    kitchendb.child("kettle").child("active").setValue(true);
                }
                else{
                    kitchendb.child("kettle").child("active").setValue(false);
                }
            }
        });

        ovenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    kitchendb.child("oven").child("active").setValue(true);
                }
                else{
                    kitchendb.child("oven").child("active").setValue(false);
                }
            }
        });

        toasterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    kitchendb.child("toaster").child("active").setValue(true);
                }
                else{
                    kitchendb.child("toaster").child("active").setValue(false);
                }
            }
        });
    }

    public void updateOvenTemp(View view) {

        Integer ovenTemp = Integer.parseInt(ovenEditText.getText().toString());

        if ((0 < ovenTemp) && (ovenTemp <= ovenMax)) {
            kitchendb.child("oven").child("temperature").setValue(ovenTemp);
            Toast.makeText(this, "Temperature Updated", Toast.LENGTH_LONG).show();
        }

        else{
            String failedString = String.format("Oven temperature cannot exceed %d", ovenMax);
            Toast.makeText(this, failedString, Toast.LENGTH_LONG).show();
        }
    }

    public void updateToasterLevel(View view) {
        Integer toasterLevel = Integer.parseInt(toasterEditText.getText().toString());

        if ((0 < toasterLevel) && (toasterLevel <= toasterMax )){
            kitchendb.child("toaster").child("level").setValue(toasterLevel);
            Toast.makeText(this, "Level Updated", Toast.LENGTH_LONG).show();
        }
        else{
            String failedString = String.format("Level must be a value between 1 and %d", toasterMax);
            Toast.makeText(this, failedString, Toast.LENGTH_LONG).show();
        }
    }
}
