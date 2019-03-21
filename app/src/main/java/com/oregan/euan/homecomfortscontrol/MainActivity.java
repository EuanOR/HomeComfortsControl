package com.oregan.euan.homecomfortscontrol;


import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;


public class MainActivity extends AppCompatActivity {

    private NotificationManager weatherNotificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    private TextView locationValueString;
    private TextView tempValueString;
    private TextView humidityValueString;
    private TextView conditionValueString;

    long repeatInterval = AlarmManager.INTERVAL_HOUR;
    long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

    //private FusedLocationProviderClient mFusedLocationClient;
    //Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        locationValueString = findViewById(R.id.location_value);
        tempValueString = findViewById(R.id.temp_value);
        humidityValueString = findViewById(R.id.humidity_value);
        conditionValueString = findViewById(R.id.condition_value);

        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        syncWithRTInfo();

        //onCreateNotificationSetup();
    }

    public void syncWithRTInfo() {
        //Calls all the async classes to update the textviews from the API
        new rtLocation(locationValueString).execute();
        new rtTemp(tempValueString).execute();
        new rtHumidity(humidityValueString).execute();
        new rtCondition(conditionValueString).execute();
    }

    public void syncButton(View view) {
        //Sets up the animations for when information is being synced.
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(1200);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(4200 + fadeIn.getStartOffset());

        //Fades out all the value textfields
        locationValueString.startAnimation(fadeOut);
        tempValueString.startAnimation(fadeOut);
        humidityValueString.startAnimation(fadeOut);
        conditionValueString.startAnimation(fadeOut);

        syncWithRTInfo();

        //Fades all the value textviews back in with updated information.
        locationValueString.startAnimation(fadeIn);
        tempValueString.startAnimation(fadeIn);
        humidityValueString.startAnimation(fadeIn);
        conditionValueString.startAnimation(fadeIn);
    }

    public void createNotificationChannel() {

        // Create a notification manager object.
        weatherNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Weather change.",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Conditions have are below threshold");
            weatherNotificationManager.createNotificationChannel(notificationChannel);

        }
    }

    private void deliverNotification(Context context) {
        Intent contentIntent = new Intent(context, MainActivity.class);

        PendingIntent contentPendingIntent =
                PendingIntent.getActivity
                        (context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Temperature change")
                .setContentText("Weather has dropped below...")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        weatherNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void onCreateNotificationSetup() {
        weatherNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();

        deliverNotification(MainActivity.this);

        Intent weatherIntent = new Intent(this, WeatherChecker.class);
        PendingIntent weatherPendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager weatherChecker = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (weatherChecker != null) {
            weatherChecker.setInexactRepeating
                    (AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            triggerTime, repeatInterval, weatherPendingIntent);
        }

        //boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, weatherIntent,
          //      PendingIntent.FLAG_NO_CREATE) != null);

    }
/*
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().
                addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            userLocation = location;
                            Toast.makeText(getApplicationContext(),location.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
*/

    public void launchHeating(View view) {
        Intent intent = new Intent(this, HeatingActivity.class);
        startActivity(intent);
    }


    public void launchLights(View view){
        Intent intent = new Intent(this, LightsActivity.class);
        startActivity(intent);
    }

    public void launchOverallControls(View view) {
        Intent intent = new Intent(this, OverallControls.class);
        startActivity(intent);
    }

    public void launchRooms(View view) {
        Intent intent = new Intent(this, RoomsActivity.class);
        startActivity(intent);
    }
}
