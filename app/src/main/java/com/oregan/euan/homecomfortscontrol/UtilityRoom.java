package com.oregan.euan.homecomfortscontrol;

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

public class UtilityRoom extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference utilitydb = database.getReference("house/rooms/utility_room");

    private String onText = "On";
    private String offText = "Off";

    private Integer dryerMax;

    private TextView dryerStateText;
    private Switch dryerSwitch;
    private EditText dryerEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility_room);
        FirebaseApp.initializeApp(this);

        dryerStateText = findViewById(R.id.dryerStateText);
        dryerSwitch = findViewById(R.id.dryerSwitch);
        dryerEditText = findViewById(R.id.dryerEditText);

        initSetup();
        switchSetup();
    }
    protected void initSetup(){
        utilitydb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean dryerState = dataSnapshot.child("dryer/active").getValue(boolean.class);
                if(dryerState){
                    dryerStateText.setText(onText);
                    dryerSwitch.setChecked(true);
                }
                else{
                    dryerStateText.setText(offText);
                    dryerSwitch.setChecked(false);
                }
                dryerMax = Math.toIntExact(dataSnapshot.child("dryer/max_temp").
                        getValue(long.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase Error:",databaseError.getDetails());
            }
        });
    }

    protected void switchSetup(){
        dryerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    utilitydb.child("dryer/active").setValue(true);
                }
                else
                    utilitydb.child("dryer/active").setValue(false);
            }
        });
    }

    public void updateDryerTemp(View view) {
        Integer dryerTemp = Integer.parseInt(dryerEditText.getText().toString());

        if ((0 < dryerTemp) && (dryerTemp <= dryerMax)){
            utilitydb.child("dryer/temperature").setValue(dryerTemp);
            Toast.makeText(this, "Temperature Updated", Toast.LENGTH_LONG).show();
        }

        else{
            String failedString = String.format("Temperature must be a value between 1 and %d", dryerMax);
            Toast.makeText(this, failedString, Toast.LENGTH_LONG).show();
        }
    }
}
