package lib.sensors.events;

import android.bluetooth.BluetoothDevice;

public interface SensorEvents {
    void Detected(BluetoothDevice Sensor);
    void Failed();
    void Removed();
    void Updated(int Frequency);
    void Selected();
}

