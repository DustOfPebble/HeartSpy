package com.dustofcloud.sensorreader;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;
import java.util.Arrays;


public class BluetoothDeviceDetector implements Runnable {

    private BluetoothAdapter Bluetooth;

    private int TimeOut = 0; // 0 means scanning forever ..
    private Handler TerminateScanning;
    private Runnable TerminateAction;
    private SensorCallBacks SensorNotify;

    public BluetoothDeviceDetector(SensorCallBacks Callback, int  TimeOut) {
        SensorNotify = Callback;
        TerminateAction = this;
        TerminateScanning = new Handler();
        this.TimeOut = TimeOut;
        Bluetooth = BluetoothAdapter.getDefaultAdapter();
    }

    // Stop scanning ...
    public void run() {
        Bluetooth.stopLeScan(DeviceFound);
        SensorNotify.SensorFound(null);
    }

    public void findHeartRateSensor(){
        Bluetooth.startLeScan(DeviceFound);
        if (TimeOut != 0) TerminateScanning.postDelayed(TerminateAction, TimeOut);
    }

    // Managing newly discovered devices
    private BluetoothAdapter.LeScanCallback DeviceFound = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice DeviceFound, final int RSSI, final byte[] scanRecord) {
            // Decoding SensorDatas
            int index = 0;
            while (index < scanRecord.length) {

                int length = scanRecord[index++];
                if (length == 0) break; //Done once we run out of records

                int type = scanRecord[index];
                if (type == 0) break; //Done if our record isn't a valid type

                if (type == SensorConstants.TYPE_UUID16) {
                    byte[] data = Arrays.copyOfRange(scanRecord, index + 1, index + length);
                    int uuid = (data[1] & 0xFF) << 8;
                    uuid += (data[0] & 0xFF);
                    String UUID = Integer.toHexString(uuid);
                    Log.d("UUID", UUID);
                    if (SensorConstants.UUID_HEART_RATE.equals(UUID)) {
                        Log.d("Bluetooth ====>", "Device found");
                        SensorNotify.SensorFound(DeviceFound);
                        break;
                    }
                }
                //Advance
                index += length;
            }
        }
    };
}

