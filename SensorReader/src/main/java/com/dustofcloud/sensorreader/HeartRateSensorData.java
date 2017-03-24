package com.dustofcloud.sensorreader;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import java.util.UUID;

public class HeartRateSensorData {

    final private String TAG = "GATT Event --->";
    private Context ProvidedContext;

    private SensorCallBacks SensorListener;

    private BluetoothGatt DataProvider;
    private BluetoothGattCharacteristic Monitor;
    private BluetoothDevice Sensor;


    public HeartRateSensorData(SensorCallBacks Listener, Context ProvidedContext){

        SensorListener = Listener;
        Sensor = null;
        this.ProvidedContext = ProvidedContext;
    }

    public void setDevice(BluetoothDevice Sensor){
        if (Sensor == null) return;
        this.Sensor = Sensor;
        DataProvider = this.Sensor.connectGatt(ProvidedContext,false,GATT_Events);
    }

    //Managing Bluetooth GATT Events ....
    private final BluetoothGattCallback GATT_Events =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt GATT_Server, int status, int newState) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        DataProvider.discoverServices();
                        SensorListener.UpdateFrequency(0);
                        return;
                    }
                    if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        SensorListener.UpdateFrequency(-1);
                        return;
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt GATT_Server, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {

                        BluetoothGattService GATT_Service = GATT_Server.getService(UUID.fromString(SensorConstants.SERVICE_HEART_RATE));
                        Monitor = GATT_Service.getCharacteristic(UUID.fromString(SensorConstants.CHARACTERISTIC_HEART_RATE));
                        GATT_Server.setCharacteristicNotification(Monitor,true);
                        BluetoothGattDescriptor MonitorSpecs = Monitor.getDescriptor(UUID.fromString(SensorConstants.DESCRIPTOR_HEART_RATE));
                        MonitorSpecs.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        GATT_Server.writeDescriptor(MonitorSpecs);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicChanged(BluetoothGatt GATT_Server, BluetoothGattCharacteristic MonitoredValue) {
                     SensorListener.UpdateFrequency(MonitoredValue.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1));
                }
            };
};