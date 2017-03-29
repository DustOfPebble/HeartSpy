package dummy.lib.sensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.BluetoothLeScanner;

import android.bluetooth.le.ScanResult;
import android.os.Handler;
import android.util.Log;

import java.util.List;

public class SensorDetector extends ScanCallback implements Runnable {

    private int TimeOut = 1000; // default TimeOut in ms ...
    private Handler TriggerEvent;
    private Runnable TerminateSearch;
    private SensorEvents SensorNotify;
    private BluetoothLeScanner DeviceScanner;
    private String LogTag = this.getClass().getSimpleName();
    private Boolean isScanning = false;

    public SensorDetector(SensorEvents Callback, int  TimeOut) {
        SensorNotify = Callback;
        TerminateSearch = this;
        TriggerEvent = new Handler();
        if (TimeOut > 1000) this.TimeOut = TimeOut;
        DeviceScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    }

    // Start scanning devices
    public void startSearch(){
        DeviceScanner.startScan(this);
        isScanning = true;
        if (TimeOut != 0) TriggerEvent.postDelayed(TerminateSearch, TimeOut);
        Log.d(LogTag, "Start scanning for "+TimeOut+" ms");
    }

    // Stop scanning on TimeOut --> TerminateSearch
    public void run() {
        if (!isScanning) return;
        isScanning = false;
        Log.d(LogTag, "Scanning reached TimeOut limit");
        DeviceScanner.stopScan(this);
        DeviceScanner.flushPendingScanResults(this);
        SensorNotify.Failed();
    }

    // Stop scanning on Request...
    public void stopSearch() {
        isScanning = false;
        DeviceScanner.stopScan(this);
        DeviceScanner.flushPendingScanResults(this);
        Log.d(LogTag, "End scanning on Listener request");
    }

    @Override
    public void onScanResult(int callbackType, ScanResult Infos) {
         BluetoothDevice SensorDevice = Infos.getDevice();
        if (SensorDevice != null ) {
            Log.d(LogTag, "Device detected --> Forwarding device to Listener");
            SensorNotify.Detected(SensorDevice);
        }
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {    }

    @Override
    public void onScanFailed(int errorCode) {
        Log.d(LogTag, "Scan failed with Error["+errorCode+"]");
    }

}

