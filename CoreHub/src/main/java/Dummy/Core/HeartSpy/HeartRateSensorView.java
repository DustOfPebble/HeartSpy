package Dummy.Core.HeartSpy;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import Dummy.Lib.Sensor.SensorDetector;
import Dummy.Lib.Sensor.SensorManager;
import Dummy.Lib.Sensor.SensorEvents;

public class HeartRateSensorView extends FrameLayout implements SensorEvents {

    public int WidthToHeightFactor = 5; // Forcing an AspectRatio of subWidget

    private BeatIndicator HeartRateIndicator=null;
    private SensorManager HeartRateProvider;

    private FileManager FilesHandler=null;
    private FileWriter WriteToFile=null;

    private long StoredStartupTime=0;

    private int SearchTimeOut = 4000; // in ms TimeOut
    SensorDetector SensorFinder;
    BluetoothDevice HeartRateSensor;

    // CallBack on Frequency Update
    @Override
    public void UpdateFrequency(int Frequency) {
        if (Frequency > 0) {
            long TimeNotified = System.currentTimeMillis();
            long ElapsedTime = TimeNotified -StoredStartupTime;
            String Snapshot= String.valueOf(ElapsedTime)+','+String.valueOf(Frequency);
            WriteToFile.appendJSON(Snapshot);
        }
        Message Informations = new Message();
        Bundle Table = new Bundle();
        Table.putInt(Constants.Frequency, Frequency);
        Informations.setData(Table);
        HeartRateIndicator.ViewUpdater.sendMessage(Informations);
    }

    // CallBack on Bluetooth Device detection
    @Override
    public void SensorFound(BluetoothDevice DiscoveredHeartRateSensor){
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

    // Alternative constructor (Seems to effectively Called) --> Crash without !
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

        HeartRateProvider = new SensorManager(this, getContext());
        SensorFinder = new SensorDetector(this, SearchTimeOut);
        SensorFinder.findHeartRateSensor();

        FilesHandler = new FileManager(context);
        WriteToFile = new FileWriter(FilesHandler);
        StoredStartupTime = System.currentTimeMillis();
    }
}
