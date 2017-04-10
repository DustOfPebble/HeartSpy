package lib.core.heartspy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import lib.events.SensorEvents;
import lib.wrist.sensor.SensorDetector;
import lib.wrist.sensor.SensorManager;


public class SensorsProvider extends Service implements SensorEvents, ServiceCommands {

    private String LogTag = this.getClass().getSimpleName();

    private NotificationManager InfoProvider;
    private Notification.Builder InfoCreator;

    private SensorManager SensorListener = null;
    private SensorDetector SensorFinder = null;
    private int SensorSearchTimeOut = 60000; // in ms TimeOut

    private SmartWatchExtension Watch = null;

    private ServiceAccess Connector=null;

    private int ServiceStatus = Constants.ServiceWaiting;
    private Bundle SensorSnapshot = null;

    public SensorsProvider(){
        SensorSnapshot = new Bundle();
        Connector = new ServiceAccess();
    }

    private void PushSystemNotification() {
        int  Info = -1;
        if (ServiceStatus == Constants.ServiceWaiting) Info = R.string.WaitingMode;
        if (ServiceStatus == Constants.ServiceRunning) Info = R.string.RunningMode;
        if (ServiceStatus == Constants.ServiceSearching) Info = R.string.SearchingMode;

        InfoCreator.setContentText(getText(Info));
        InfoProvider.notify(R.string.ID,InfoCreator.build());
    }

    /**************************************************************
     *  Callbacks implementation for
     *  - Sensor detection
     *  - Sensor selection
     *  - Sensor update value
     *  - Sensor disconnection
     **************************************************************/
    @Override
    public void Updated(int Value) {
        SensorSnapshot.clear();
        SensorSnapshot.putInt(Constants.SensorValue, Value);
        Watch.push(SensorSnapshot);

        Connector.Update(Value);
    }

    @Override
    public void Detected(BluetoothDevice DiscoveredSensor){
        if (DiscoveredSensor == null) return;
        SensorListener.checkDevice(DiscoveredSensor);
    }
    @Override
    public void Selected(){
        SensorFinder.stopSearch();

        ServiceStatus = Constants.ServiceRunning;
        PushSystemNotification();

        Connector.StateChanged(ServiceStatus);

        SensorSnapshot.clear();
        SensorSnapshot.putBoolean(Constants.SensorSelected, true);
        Watch.push(SensorSnapshot);
    }

    @Override
    public void Failed(){
        ServiceStatus = Constants.ServiceWaiting;
        PushSystemNotification();

        Connector.StateChanged(ServiceStatus);

        SensorSnapshot.clear();
        SensorSnapshot.putBoolean(Constants.SensorSelected, false);
        Watch.push(SensorSnapshot);
    }

    @Override
    public void Removed(){
        ServiceStatus = Constants.ServiceWaiting;
        PushSystemNotification();

        Connector.StateChanged(ServiceStatus);

        SensorSnapshot.clear();
        SensorSnapshot.putBoolean(Constants.SensorSelected, false);
        Watch.push(SensorSnapshot);
    }

    /**************************************************************
     *  Callbacks implementation for Service management
     **************************************************************/
    @Override
    public void onCreate(){
        super.onCreate();
        InfoProvider = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        InfoCreator = new Notification.Builder(this);
        InfoCreator.setSmallIcon(R.drawable.icon_heartspy);
        InfoCreator.setContentTitle(getText(R.string.ServiceName));

        PushSystemNotification();

        SensorListener = new SensorManager(this, getBaseContext());
        SensorFinder = new SensorDetector(this, SensorSearchTimeOut);
        Watch = new SmartWatchExtension(getBaseContext());

        Connector.RegisterProvider(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LogTag, "Starting service ...");

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LogTag, "Binding service ...");
        return Connector;
    }

    @Override
    public void onDestroy() {
        Log.d(LogTag, "Service is about to quit !");
        InfoProvider.cancel(R.string.ID);
        super.onDestroy();
    }

    /**************************************************************
     *  Callbacks implementation for incoming messages
     **************************************************************/
    @Override
    public void SearchSensor() {
        if (ServiceStatus == Constants.ServiceSearching) return;
        SensorFinder.startSearch();
        ServiceStatus = Constants.ServiceSearching;
        PushSystemNotification();

        Connector.StateChanged(ServiceStatus);
    }

    @Override
    public void Stop() {
        if (ServiceStatus == Constants.ServiceSearching) {
            SensorFinder.stopSearch();
            ServiceStatus = Constants.ServiceWaiting;
            PushSystemNotification();
            Connector.StateChanged(ServiceStatus);
        }
        if (ServiceStatus == Constants.ServiceRunning) {
            SensorListener.disconnect();
        }
    }

    @Override
    public void Query() {
        Connector.StateChanged(ServiceStatus);
    }



}
