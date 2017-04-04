package lib.wrist.sensor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import lib.sensors.events.Events;

public class SensorManager extends BluetoothGattCallback{

    private String LogTag = this.getClass().getSimpleName();

    private Context SavedContext;
    private Events SensorListener;
    private BluetoothDevice SelectedSensor = null;

    public SensorManager(Events Listener, Context context){
        SensorListener = Listener;
        this.SavedContext = context;
    }

    public void checkDevice(BluetoothDevice Sensor){
        if (Sensor == null) return;
        if (SelectedSensor != null) return;
        Sensor.connectGatt(SavedContext,false,this);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt DeviceSocket, int status, int newState) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            DeviceSocket.discoverServices();
            Log.d(LogTag, "Connected to Device server --> Starting Services discovery");
            return;
        }
        if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            BluetoothDevice DisconnectedDevice = DeviceSocket.getDevice();
            DeviceSocket.close();
            if (DisconnectedDevice != SelectedSensor) { Log.d(LogTag, "Unselected Device has disconnected..."); return;}
            SensorListener.Removed();
            SelectedSensor = null;
            Log.d(LogTag, "Selected Device has disconnected.");
            return;
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt DeviceSocket, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(LogTag, "Services discovered --> Checking for matching service");
            BluetoothGattService DeviceService = DeviceSocket.getService(SensorConstants.SERVICE_HEART_RATE);
            if ( DeviceService == null ) {
                DeviceSocket.disconnect();
                Log.d(LogTag, "Device not providing expected service --> Disconnecting");
                return;
            }
            Log.d(LogTag, "Matching Device server found --> Configuring device");
            SensorListener.Selected();
            SelectedSensor = DeviceSocket.getDevice();

            BluetoothGattCharacteristic Monitor = DeviceService.getCharacteristic(SensorConstants.CHARACTERISTIC_HEART_RATE);
            DeviceSocket.setCharacteristicNotification(Monitor,true);

            BluetoothGattDescriptor MonitorSpecs = Monitor.getDescriptor(SensorConstants.DESCRIPTOR_HEART_RATE);
            MonitorSpecs.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            DeviceSocket.writeDescriptor(MonitorSpecs);
        }
     }

    @Override
    public void onCharacteristicChanged(BluetoothGatt DeviceSocket, BluetoothGattCharacteristic MonitoredValue) {
        int SensorValue = MonitoredValue.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
        SensorListener.Updated(SensorValue);
        Log.d(LogTag, "Updating --> Value["+SensorValue+"]");
    }
}
