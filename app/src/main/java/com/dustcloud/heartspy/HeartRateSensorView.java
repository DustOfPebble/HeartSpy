package com.dustcloud.heartspy;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

public class HeartRateSensorView extends FrameLayout implements FrequencyUpdatedCallBack,SensorsFoundCallBack {

    public int WidthToHeightFactor = 5; // Forcing an AspectRatio of subWidget

    private BeatIndicator HeartRateIndicator=null;
    private HeartRateSensorData HeartRateProvider;

    private FileManager FilesHandler=null;
    private FileWriter WriteToFile=null;

    private long StoredStartupTime=0;


    private int SearchTimeOut = 4000; // 10 secondes TimeOut
    BluetoothDeviceDetector SensorFinder;
    BluetoothDevice HeartRateSensor;

    // CallBack on Frequency Update
    @Override
    public void UpdateFrequency(final int Frequency) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (Frequency < 0) {
                    HeartRateIndicator.setConnectedState(false);
                    HeartRateIndicator.setHeartRate(0);
                    HeartRateSensor = null;
                    SensorFinder.findHeartRateSensor();
                } else {
                    HeartRateIndicator.setConnectedState(true);
                    HeartRateIndicator.setHeartRate(Frequency);
                    long TimeNotified = System.currentTimeMillis();
                    long ElapsedTime = TimeNotified -StoredStartupTime;
                    String HeartbeatSnapshot= String.valueOf(ElapsedTime)+','+String.valueOf(Frequency);
                    WriteToFile.appendJSON(HeartbeatSnapshot);
                }
            }
        });
    }

    // CallBack on Bluetooth Device detection
    @Override
    public void HeartRateSensorFound(BluetoothDevice DiscoveredHeartRateSensor){
        if (DiscoveredHeartRateSensor == null) { // We have reach a TimeOut ...
            if (HeartRateSensor == null) {
                SensorFinder.findHeartRateSensor(); // No Device found --> Continue to search
            } else {
                HeartRateProvider.setDevice(HeartRateSensor);
            }
        } else { HeartRateSensor = DiscoveredHeartRateSensor; }

    }


    // Default constructor (Seems to be Not mandatory)
    public HeartRateSensorView(Context context)
    {
        super(context);
        initObjects(context);
    }

    // Atenative constructor (Seems to effectively Called) --> Crash without !
    public HeartRateSensorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initObjects(context);
    }

    private void initObjects(Context context)
    {
        // Inflate the Layout from XML definition
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.hear_rate_sensor_view, this, true);
        HeartRateIndicator = (BeatIndicator) findViewById(R.id.beat_indicator);
        HeartRateIndicator.WidthToHeightFactor = WidthToHeightFactor;

        HeartRateProvider = new HeartRateSensorData(this, getContext());
        SensorFinder = new BluetoothDeviceDetector(this, SearchTimeOut);
        SensorFinder.findHeartRateSensor();

        WriteToFile = new FileWriter(FilesHandler);
        StoredStartupTime = System.currentTimeMillis();

    }
}
