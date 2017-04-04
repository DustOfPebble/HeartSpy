package lib.sensors.events;

import android.bluetooth.BluetoothDevice;

public interface Events {
    void Detected(BluetoothDevice Sensor);
    void Failed();
    void Removed();
    void Updated(int Frequency);
    void Selected();
}

