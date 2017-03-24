package com.dustofcloud.sensorreader;

import android.bluetooth.BluetoothDevice;

public interface SensorCallBacks {
        void HeartRateSensorFound(BluetoothDevice Sensor);
        void UpdateFrequency(int Frequency);
}

