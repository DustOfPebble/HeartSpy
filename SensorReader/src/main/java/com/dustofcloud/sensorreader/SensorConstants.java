package com.dustofcloud.sensorreader;

import java.util.UUID;

public class SensorConstants {
    // Used During Scanning
    // static final int TYPE_UUID16 = 0x3; // UUID id Expected Format
    // static final String UUID_HEART_RATE = "180d"; // MIO GLOBAL LINK Heart Rate Service

    // Used by Bluetooth GATT connection
    // static final String CHARACTERISTIC_HEART_RATE = "00002a37-0000-1000-8000-00805f9b34fb";
    static final UUID CHARACTERISTIC_HEART_RATE = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    // static final String SERVICE_HEART_RATE = "0000180d-0000-1000-8000-00805f9b34fb";
    static final UUID SERVICE_HEART_RATE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    // static final String DESCRIPTOR_HEART_RATE = "00002902-0000-1000-8000-00805f9b34fb";
    static final UUID  DESCRIPTOR_HEART_RATE = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
}
