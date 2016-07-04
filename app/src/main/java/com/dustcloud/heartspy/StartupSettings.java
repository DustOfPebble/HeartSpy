package com.dustcloud.heartspy;

import android.app.Activity;
import android.os.Bundle;
//ToDo : Make it the main interface

public class StartupSettings extends Activity {

    private HeartRateSensorView HeartRateSensorFrame;
    private SmartWatchExchangeView SmartWatchExchangeFrame;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creating View from XML
        setContentView(R.layout.startup_settings);
        HeartRateSensorFrame = (HeartRateSensorView) findViewById(R.id.heart_rate_sensor_view);
        SmartWatchExchangeFrame = (SmartWatchExchangeView) findViewById(R.id.smartwatch_exchange_view);

    }



}

