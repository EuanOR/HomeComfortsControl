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

public class BedroomActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference bedroomdb = database.getReference("house/rooms/bedroom");

    private String onText = "On";
    private String offText = "Off";

    private Integer ebMax;

    private TextView ebStateText;
    private EditText ebEditText;
    private Switch ebSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bedroom);
        FirebaseApp.initializeApp(this);

        ebStateText = findViewById(R.id.ebStateText);
        ebEditText = findViewById(R.id.ebLevelEditText);
        ebSwitch = findViewById(R.id.ebSwitch);

        initSetup();
        switchSetup();
    }

    protected void initSetup(){
        bedroomdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean ebState = dataSnapshot.child("electric_blanket/active")
                        .getValue(boolean.class);
                if (ebState){
                    ebStateText.setText(onText);
                    ebSwitch.setChecked(true);
                }
                else{
                    ebStateText.setText(offText);
                    ebSwitch.setChecked(false);
                }
                ebMax = Math.toIntExact(dataSnapshot.child("electric_blanket/max_level")
                .getValue(long.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase Error:", databaseError.getDetails());
            }
        });
    }

    protected void switchSetup(){
        ebSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    bedroomdb.child("electric_blanket/active").setValue(true);
                }
                else{
                    bedroomdb.child("electric_blanket/active").setValue(false);
                }
            }
        });
    }

    public void updateEBLevel(View view) {
        Integer ebLevel = Integer.parseInt(ebEditText.getText().toString());

        if ((0 < ebLevel) && (ebLevel <=  ebMax)){
            bedroomdb.child("electric_blanket/level").setValue(ebLevel);
            Toast.makeText(this, "Level Updated", Toast.LENGTH_LONG).show();
        }
        else{
            String failedString = String.format("Level must be a value between 1 and %d", ebMax);
            Toast.makeText(this, failedString, Toast.LENGTH_LONG).show();
        }
    }
}