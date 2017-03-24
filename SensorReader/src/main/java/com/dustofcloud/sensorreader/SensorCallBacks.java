package com.dustofcloud.sensorreader;

import android.bluetooth.BluetoothDevice;

public interface SensorCallBacks {
        void SensorFound(BluetoothDevice Sensor);
        void UpdateFrequency(int Frequency);
}

