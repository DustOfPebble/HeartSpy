package lib.core.heartspy;

import android.content.Context;
import android.os.Bundle;

import lib.smartwatch.SmartwatchBundle;
import lib.smartwatch.SmartwatchEvents;
import lib.smartwatch.SmartwatchManager;

public class SmartWatchExtension implements SmartwatchEvents {

    private SmartwatchManager WatchConnector = null;
    private SmartwatchBundle DataSet = null;
    private Boolean isWatchConnected = false;


    public SmartWatchExtension(Context context) {
        WatchConnector = new SmartwatchManager(context,this,WatchConstants.WatchUUID);
        isWatchConnected = WatchConnector.isConnected();
    }

    void push(Bundle Values) {
        if (!isWatchConnected) return;
        for (String key : Values.keySet()) {
            if (key == Constants.SensorValue)
                DataSet.update(WatchConstants.SensorValue, Values.getInt(key));
        }

        WatchConnector.send(DataSet);

        DataSet = new SmartwatchBundle();

    }

    @Override
    public void ConnectedStateChanged(Boolean ConnectState) {
        isWatchConnected = ConnectState;
    }


}
