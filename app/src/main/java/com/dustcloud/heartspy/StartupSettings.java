package com.dustcloud.heartspy;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import static android.content.ContentValues.TAG;
//ToDo : Make it the main interface

public class StartupSettings extends Activity {

    private HeartRateSensorView HeartRateSensorFrame;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
            // Creating View from XML
        setContentView(R.layout.startup_settings);
            HeartRateSensorFrame = (HeartRateSensorView) findViewById(R.id.heart_rate_sensor_view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                }
            }
        }
    }
}

