package com.dustcloud.heartspy;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.bluetooth.BluetoothAdapter;
import android.widget.Toast;

public class SharedDatas extends Application {

    SharedPreferences StoredSettings= null;

    @Override
    public void onCreate()
    {
        super.onCreate();

        // Loading stored settings and options ...
        StoredSettings = getSharedPreferences(Constants.PACKAGE_IDENTIFIER, MODE_PRIVATE);
    }

    // Collection of callable functions
    public void updateBPM(int BPM) {
    }

    public void updateGPS(double Longitude, double Latitude ) {
    }

}
