package dummy.lib.sensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.BluetoothLeScanner;

import android.bluetooth.le.ScanResult;
import android.os.Handler;

import java.util.List;

public class SensorDetector extends ScanCallback implements Runnable {

    private int TimeOut = 0; // 0 means scanning forever ..
    private Handler TerminateScanning;
    private Runnable TerminateAction;
    private SensorEvents SensorNotify;
    private BluetoothLeScanner DeviceScanner;

    public SensorDetector(SensorEvents Callback, int  TimeOut) {
        SensorNotify = Callback;
        TerminateAction = this;
        TerminateScanning = new Handler();
        this.TimeOut = TimeOut;
        DeviceScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    }

    // Start scanning devices
    public void findHeartRateSensor(){
        DeviceScanner.startScan(this);
        if (TimeOut != 0) TerminateScanning.postDelayed(TerminateAction, TimeOut);
    }

    // Stop scanning ...
    public void run() {
        DeviceScanner.stopScan(this);
        SensorNotify.SensorFound(null);
    }

    @Override
    public void onScanResult(int callbackType, ScanResult Infos) {
        BluetoothDevice SensorDevice = Infos.getDevice();
        if (SensorDevice != null ) SensorNotify.SensorFound(Infos.getDevice());
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        SensorNotify.SensorFound(null);
    }

    @Override
    public void onScanFailed(int errorCode) {
        SensorNotify.SensorFound(null);
    }

}

