package com.github.guikeller.cordova.wearos;

import android.os.Binder;
import android.util.Log;

public class WearOsBinder extends Binder {

    private static final String TAG = WearOsBinder.class.getSimpleName();
    private static WearOsListenerService service;

    public WearOsBinder(WearOsListenerService service) {
        Log.i(TAG, "constructor");
        this.service = service;
    }

    protected WearOsListenerService getService() {
        Log.i(TAG, "getService");
        return service;
    }

}
