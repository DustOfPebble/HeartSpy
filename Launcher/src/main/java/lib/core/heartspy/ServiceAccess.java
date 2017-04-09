package lib.core.heartspy;

import android.os.Binder;
import android.util.Log;

public class ServiceAccess extends Binder {

    private String LogTag = this.getClass().getSimpleName();

    private ServiceCommands Service = null;
    private UpdateEvents Updater = null;

    public void RegisterProvider(ServiceCommands Provider) { Service = Provider; }
    public void RegisterListener(UpdateEvents Listener) { Updater = Listener; }

    public void StartSearch() { Service.StartSearch(); }
    public void StopSearch() { Service.StopSearch(); }

    public void Update(int Value) {
        try { Updater.Update(Value);}
        catch (Exception Failed) { Log.d(LogTag, "Failed on Update event");}
    }

    public void StateChanged(int State) {
        Updater.StateChanged(State);
        try {Updater.StateChanged(State); }
        catch (Exception Failed) { Log.d(LogTag, "Failed on StateChanged event");}
    }

}
