package dummy.core.heartspy;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import java.util.Random;

import dummy.lib.sensor.SensorDetector;
import dummy.lib.sensor.SensorManager;
import dummy.lib.sensor.SensorEvents;

import dummy.lib.smartwatch.SmartwatchBundle;
import dummy.lib.smartwatch.SmartwatchEvents;
import dummy.lib.smartwatch.SmartwatchManager;

public class HeartRateSensorView extends FrameLayout implements SensorEvents, SmartwatchEvents, Runnable {

    private String LogTag = this.getClass().getSimpleName();

    public int WidthToHeightFactor = 5; // Forcing an AspectRatio of subWidget

    private BeatIndicator VisualIndicator = null;
    private SensorManager SensorListener = null;
    private SensorDetector SensorFinder = null;

    private SmartwatchBundle DataSet = null;
    private SmartwatchManager WatchConnector = null;
    private Boolean isWatchConnected = false;

    private FileManager FilesHandler = null;
    private FileWriter LogWriter = null;

    private Bundle Table = null;
    private Message Informations = null;

    private long StoredStartupTime=0;
    private long TimeNotified = 0;
    private long ElapsedTime = 0;
    private String Snapshot = "";

    private int SearchTimeOut = 60000; // in ms TimeOut

    private Handler Event = null;
    private int EventCount = 0;
    private int EventDelay = 1000; // in ms


    // CallBack on Frequency Updated
    @Override
    public void Updated(int Frequency) {
        WriteLog(Frequency);
        UpdateView(Frequency, true);
        UpdateWatchView(Frequency);
    }

    private void WriteLog(int Value) {
        TimeNotified = System.currentTimeMillis();
        ElapsedTime = TimeNotified - StoredStartupTime;
        Snapshot= String.valueOf(ElapsedTime)+','+String.valueOf(Value);
        LogWriter.appendJSON(Snapshot);
    }

    private void UpdateView(int Value, boolean Connected) {
        Informations = VisualIndicator.ViewUpdater.obtainMessage();
        Table.clear();
        Table.putInt(Constants.Frequency, Value);
        Table.putBoolean(Constants.Connected, Connected);
        Informations.setData(Table);
        VisualIndicator.ViewUpdater.sendMessage(Informations);
    }

    private void UpdateWatchView(int Frequency) {
        DataSet.update(Constants.SensorValue,Frequency);
        if (!isWatchConnected) return;
        WatchConnector.send(DataSet);
    }

    // CallBack on Bluetooth Device detection
    @Override
    public void Detected(BluetoothDevice DiscoveredSensor){
        if (DiscoveredSensor == null) return;
        SensorListener.checkDevice(DiscoveredSensor);
    }
    @Override
    public void Selected(){
        UpdateView(0, true);
        SensorFinder.stopSearch();
    }

    @Override
    public void Failed(){
        SensorFinder.startSearch();
    }

    @Override
    public void Removed(){
        UpdateView(0, false);
        SensorFinder.startSearch();
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
        VisualIndicator = (BeatIndicator) findViewById(R.id.beat_indicator);
        VisualIndicator.WidthToHeightFactor = WidthToHeightFactor;

        SensorListener = new SensorManager(this, getContext());
        SensorFinder = new SensorDetector(this, SearchTimeOut);
        SensorFinder.startSearch();

        FilesHandler = new FileManager(context);
        LogWriter = new FileWriter(FilesHandler);
        StoredStartupTime = System.currentTimeMillis();

        Table = new Bundle();
        DataSet = new SmartwatchBundle();
        WatchConnector = new SmartwatchManager(context,this,Constants.WatchUUID);
        isWatchConnected = WatchConnector.isConnected();

        // Start Event simulator
        Event = new Handler();
        this.run();
    }

    @Override
    public void ConnectedStateChanged(Boolean ConnectState) {
        isWatchConnected = ConnectState;
    }

    // Force a Write to the Smartwatch ...
    @Override
    public void run() {
        EventCount++;
        if (EventCount>220) EventCount=40;
        DataSet.update(Constants.SensorValue,EventCount);
        if (!isWatchConnected) return;
        WatchConnector.send(DataSet);
        Event.postDelayed(this, EventDelay);
    }
}
