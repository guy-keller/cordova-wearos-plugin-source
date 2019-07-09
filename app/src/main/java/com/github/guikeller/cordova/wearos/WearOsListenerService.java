package com.github.guikeller.cordova.wearos;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class WearOsListenerService extends WearableListenerService {

    private static final String TAG = WearOsListenerService.class.getSimpleName();
    private static final String MSG_PATH = "/cordova/plugin/wearos";

    private static WearOsMessageListener messageListener;

    public WearOsListenerService(){
        super();
        Log.i(TAG, "constructor");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i(TAG, "onMessageReceived");
        if (MSG_PATH.equals(messageEvent.getPath()) && messageListener != null) {
            String msg = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            messageListener.messageReceived(msg);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

    protected void registerMessageListener(WearOsMessageListener messageListener) {
        Log.i(TAG, "registerMessageListener");
        this.messageListener = messageListener;
    }

}
