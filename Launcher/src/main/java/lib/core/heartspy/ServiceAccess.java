package lib.core.heartspy;

import android.os.Binder;

public class ServiceAccess extends Binder {

    private ServiceCommands Service = null;
    private UpdateEvents Updater = null;

    public void RegisterProvider(ServiceCommands Provider) { Service = Provider; }
    public void RegisterListener(UpdateEvents Listener) { Updater = Listener; }

    public void StartSearch() { Service.StartSearch(); }
    public void StopSearch() { Service.StopSearch(); }

    public void Update(int Value) { Updater.Update(Value); }
    public void StateChanged(int State) { Updater.StateChanged(State); }

}
