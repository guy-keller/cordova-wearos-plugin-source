package com.github.guikeller.cordova.wearos;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * The connection between the provider (us) and consumer (client)
 * @author guikeller
 */
public class WearOsServiceConnection implements ServiceConnection {

    private static final String TAG = WearOsServiceConnection.class.getSimpleName();
    private boolean serviceBound;
    private IBinder service;

    public WearOsServiceConnection() {
        super();
        Log.i(TAG,"constructor");
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i(TAG,"onServiceConnected :: service: "+service);
        this.service = service;
        this.serviceBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i(TAG,"onServiceDisconnected");
        this.service = null;
        this.serviceBound = false;
    }

    public WearOsListenerService getService(){
        Log.i(TAG,"getService");
        WearOsListenerService service = null;
        if (this.serviceBound && this.service != null) {
            // Through the binder we can get hold of the service / agent
            WearOsBinder binder = (WearOsBinder) this.service;
            service = binder.getService();
        }
        return service;
    }

}
