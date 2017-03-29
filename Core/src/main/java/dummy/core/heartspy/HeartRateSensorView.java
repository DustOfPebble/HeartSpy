package dummy.core.heartspy;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import dummy.lib.sensor.SensorDetector;
import dummy.lib.sensor.SensorManager;
import dummy.lib.sensor.SensorEvents;

public class HeartRateSensorView extends FrameLayout implements SensorEvents {

    public int WidthToHeightFactor = 5; // Forcing an AspectRatio of subWidget

    private BeatIndicator VisualIndicator = null;
    private SensorManager SensorListener = null;
    private SensorDetector SensorFinder = null;

    private FileManager FilesHandler = null;
    private FileWriter LogWriter = null;

    private Bundle Table = null;
    private Message Informations = null;

    private long StoredStartupTime=0;
    private long TimeNotified = 0;
    private long ElapsedTime = 0;
    private String Snapshot = "";

    private int SearchTimeOut = 10000; // in ms TimeOut

    // CallBack on Frequency Updated
    @Override
    public void Updated(int Frequency) {
        if (Frequency > 0) {
            TimeNotified = System.currentTimeMillis();
            ElapsedTime = TimeNotified - StoredStartupTime;
            Snapshot= String.valueOf(ElapsedTime)+','+String.valueOf(Frequency);
            LogWriter.appendJSON(Snapshot);
        }
        Informations = VisualIndicator.ViewUpdater.obtainMessage();
        Table.clear();
        Table.putInt(Constants.Frequency, Frequency);
        Informations.setData(Table);
        VisualIndicator.ViewUpdater.sendMessage(Informations);
    }

    // CallBack on Bluetooth Device detection
    @Override
    public void Detected(BluetoothDevice DiscoveredSensor){
        if (DiscoveredSensor == null) return;
        SensorListener.checkDevice(DiscoveredSensor);
    }
    @Override
    public void Selected(){ SensorFinder.stopSearch(); }

    @Override
    public void Failed(){ SensorFinder.startSearch(); }

    @Override
    public void Removed(){ SensorFinder.startSearch(); }

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
    }
}
