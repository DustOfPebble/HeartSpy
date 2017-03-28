package dummy.lib.sensor;

import android.bluetooth.BluetoothDevice;

public interface SensorEvents {
        void SensorFound(BluetoothDevice Sensor);
        void UpdateFrequency(int Frequency);
}

