package Dummy.Lib.Sensor;

import android.bluetooth.BluetoothDevice;

public interface SensorEvents {
        void SensorFound(BluetoothDevice Sensor);
        void UpdateFrequency(int Frequency);
}

