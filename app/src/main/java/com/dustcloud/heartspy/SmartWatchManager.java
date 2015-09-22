package com.dustcloud.heartspy;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

public class SmartWatchManager {
    private WatchReceiver MessageManager;
    boolean WatchConnected = false;
    SmartWatchNotifyCallBack Receiver;

    public SmartWatchManager(SmartWatchNotifyCallBack Caller, Context ProvidedContext) {
         WatchConnected = PebbleKit.isWatchConnected(ProvidedContext);
         this.Receiver = Caller;

        MessageManager = new WatchReceiver();
        IntentFilter Filter = new IntentFilter();
        Filter.addAction(Constants.INTENT_PEBBLE_CONNECTED);
        Filter.addAction(Constants.INTENT_PEBBLE_DISCONNECTED);
        Filter.addAction(Constants.INTENT_APP_RECEIVE);
        Filter.addAction(Constants.INTENT_APP_ACK);
        Filter.addAction(Constants.INTENT_APP_NACK);
        ProvidedContext.registerReceiver(MessageManager, Filter);
   }

    public boolean isConnected() {
        return WatchConnected;
    }

    // Setting a receiver
    private class WatchReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            if (intent == null) return;
            String Operation = intent.getAction();
            if (Operation==null) return;

            Log.i("WatchManager:", "Intent catched");

            // Connections Management
            if (Operation.equals(Constants.INTENT_PEBBLE_CONNECTED))
            { // a PebbleWatch get connected
                WatchConnected = true;
                Receiver.ConnectedStateChanged(WatchConnected);
            }

            if (Operation.equals(Constants.INTENT_PEBBLE_DISCONNECTED))
            { // a PebbleWtach get disconnected
                WatchConnected = false;
                Receiver.ConnectedStateChanged(WatchConnected);
            }

            // Received Data ...
            if (Operation.equals(Constants.INTENT_APP_RECEIVE))
            { // Pending datas have been received

            }

            if (Operation.equals(Constants.INTENT_APP_ACK))
            { // Data sent to Peeble have been acknowleged

            }

            if (Operation.equals(Constants.INTENT_APP_NACK))
            { // Data sent to Peeble have not been acknowleged

            }

        }

    }

}


