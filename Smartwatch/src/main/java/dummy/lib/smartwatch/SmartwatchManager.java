package dummy.lib.smartwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;

import java.util.UUID;

public class SmartwatchManager extends BroadcastReceiver {

    private String LogTag = this.getClass().getSimpleName();

    boolean WatchConnected = false;

    private UUID Identifier;
    private Context SavedContext;

    SmartwatchEvents Listener;

    public SmartwatchManager(SmartwatchEvents Caller, Context ProvidedContext) {
        Listener = Caller;
        SavedContext =  ProvidedContext;

        IntentFilter Filter = new IntentFilter();
        Filter.addAction(Constants.INTENT_PEBBLE_CONNECTED);
        Filter.addAction(Constants.INTENT_PEBBLE_DISCONNECTED);
        Filter.addAction(Constants.INTENT_APP_RECEIVE);
        Filter.addAction(Constants.INTENT_APP_ACK);
        Filter.addAction(Constants.INTENT_APP_NACK);
        SavedContext.registerReceiver(this, Filter);
    }

    public boolean isConnected() {
        return PebbleKit.isWatchConnected(SavedContext);
    }

    public void setID(String Signature) {
        Identifier = UUID.fromString(Signature);
    }

    public void send(SmartwatchBundle DataSet) {
        if (!isConnected()) return;
        PebbleKit.sendDataToPebble(SavedContext, Identifier, DataSet);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(LogTag, "Intent received");
        if (intent == null) return;
        String Operation = intent.getAction();
        if (Operation==null) return;

        Log.d(LogTag, "Intent catched");

        // Connections Management
        if (Operation.equals(Constants.INTENT_PEBBLE_CONNECTED))
        {
            WatchConnected = true;
            Listener.ConnectedStateChanged(WatchConnected);
        }

        if (Operation.equals(Constants.INTENT_PEBBLE_DISCONNECTED))
        {
            WatchConnected = false;
            Listener.ConnectedStateChanged(WatchConnected);
        }

        // Received Data ...
        if (Operation.equals(Constants.INTENT_APP_RECEIVE))
        {
            Log.d(LogTag, "Intent ==> APP_RECEIVE");
        }

        if (Operation.equals(Constants.INTENT_APP_ACK))
        {
            Log.d(LogTag, "Intent ==> INTENT_APP_ACK");
        }

        if (Operation.equals(Constants.INTENT_APP_NACK))
        {
            Log.d(LogTag, "Intent ==> INTENT_APP_ACK");
        }
    }
}


