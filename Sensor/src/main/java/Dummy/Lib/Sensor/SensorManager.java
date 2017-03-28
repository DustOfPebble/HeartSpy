package Dummy.Lib.Sensor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

public class SensorManager extends BluetoothGattCallback{

    private Context SavedContext;
    private SensorEvents SensorListener;

    public SensorManager(SensorEvents Listener, Context context){
        SensorListener = Listener;
        this.SavedContext = context;
    }

    public void setDevice(BluetoothDevice Sensor){
        if (Sensor == null) return;
        Sensor.connectGatt(SavedContext,false,this);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt DeviceServer, int status, int newState) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            DeviceServer.discoverServices();
            SensorListener.UpdateFrequency(0);
            return;
        }
        if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            SensorListener.UpdateFrequency(-1);
            return;
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt DeviceServer, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            BluetoothGattService DeviceService = DeviceServer.getService(SensorConstants.SERVICE_HEART_RATE);
            if ( DeviceService == null ) {
                DeviceServer.disconnect();
                return;
            }

            BluetoothGattCharacteristic Monitor = DeviceService.getCharacteristic(SensorConstants.CHARACTERISTIC_HEART_RATE);
            DeviceServer.setCharacteristicNotification(Monitor,true);

            BluetoothGattDescriptor MonitorSpecs = Monitor.getDescriptor(SensorConstants.DESCRIPTOR_HEART_RATE);
            MonitorSpecs.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            DeviceServer.writeDescriptor(MonitorSpecs);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt DeviceServer, BluetoothGattCharacteristic MonitoredValue) {
        SensorListener.UpdateFrequency(MonitoredValue.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1));
    }
}
