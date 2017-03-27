package com.dustofcloud.sensorreader;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.BluetoothLeScanner;

import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.Handler;
import android.util.Log;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BluetoothDeviceDetector extends ScanCallback implements Runnable {

    private int TimeOut = 0; // 0 means scanning forever ..
    private Handler TerminateScanning;
    private Runnable TerminateAction;
    private SensorCallBacks SensorNotify;
    private BluetoothLeScanner DeviceScanner;

    public BluetoothDeviceDetector(SensorCallBacks Callback, int  TimeOut) {
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
/*        byte[] Properties = Infos.getScanRecord().getBytes();
        int index = 0;
        while (index < Properties.length) {

            int length = Properties[index++];
            if (length == 0) break; //Done once we run out of records

            int type = Properties[index];
            if (type == 0) break; //Done if our record isn't a valid type

            if (type == SensorConstants.TYPE_UUID16) {
                byte[] data = Arrays.copyOfRange(Properties, index + 1, index + length);
                int uuid = (data[1] & 0xFF) << 8;
                uuid += (data[0] & 0xFF);
                String UUID = Integer.toHexString(uuid);
                Log.d("UUID", UUID);
                if (SensorConstants.UUID_HEART_RATE.equals(UUID)) {
                    Log.d("Bluetooth ====>", "Device found");
                    SensorNotify.SensorFound(Infos.getDevice());
                    break;
                }
            }
            index += length;
        }
*/
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

