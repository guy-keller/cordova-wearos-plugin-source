package com.github.guikeller.cordova.wearos;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.Wearable;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.nio.charset.StandardCharsets;

public class CordovaWearOsPlugin extends CordovaPlugin {

    private static final String TAG = CordovaWearOsPlugin.class.getSimpleName();
    private static final String MSG_PATH = "/cordova/plugin/wearos";

    private static WearOsMessageListener listener;
    private static CordovaInterface cordovaInterface;
    private static CallbackContext callbackContext;

    public CordovaWearOsPlugin(){
        super();
        Log.i(TAG, "constructor");
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Log.i(TAG, "initialize");
        super.initialize(cordova, webView);
        cordovaInterface = cordova;
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, "execute");
        CordovaWearOsPluginAction pluginAction = CordovaWearOsPluginAction.fromValue(action);
        switch (pluginAction){
            case INIT:
                init(callbackContext);
                break;
            case SHUTDOWN:
                shutdown(callbackContext);
                break;
            case SEND_MESSAGE:
                sendMessage(args, callbackContext);
                break;
            case REGISTER_MESSAGE_LISTENER:
                registerMessageListener(callbackContext);
                break;
            default:
                return false;
        }
        return true;
    }

    protected void init(CallbackContext callbackContext){
        Log.i(TAG,"init");
        callbackContext.success();
    }

    protected void shutdown(CallbackContext callbackContextParam) {
        Log.i(TAG,"shutdown");
        listener = null;
        callbackContext = null;
        callbackContextParam.success();
    }

    protected void registerMessageListener(CallbackContext callbackContextParam) {
        Log.i(TAG,"registerMessageListener :: listener: "+callbackContextParam);
        // Keeping a reference of the 'callbackContextParam'
        callbackContext = callbackContextParam;
        // Creating the listener, the listener is fired
        // when we receive a message from the WearOS app
        listener = createWearOsMessageListener();
        // Registering to receive messages from the wear
        Activity context = cordovaInterface.getActivity();
        registerWearableMessageListener(context);
        // Returning 'OK' but keeping the callback - so we can use whenever
        // we receive another message from the Wearable / Companion app
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }

    protected void sendMessage(JSONArray args, CallbackContext callbackContext){
        try {
            Log.i(TAG,"sendMessage :: args: "+args);
            if (args != null) {
                Activity context = cordovaInterface.getActivity();
                WearOsMessageSender sender = new WearOsMessageSender(context);
                String msg = args.getString(0);
                sender.sendMessage(msg);
                callbackContext.success();
            }
        } catch (Exception ex){
            callbackContext.error("Not able to send message: " + ex.getMessage());
        }
    }

    @SuppressWarnings("all")
    protected WearOsMessageListener createWearOsMessageListener(){
        Log.i(TAG,"createWearOsMessageListener");
        WearOsMessageListener listener = new WearOsMessageListener() {
            @Override
            public void messageReceived(String msg) {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, msg);
                pluginResult.setKeepCallback(true);
                // This is the callback that we kept on the 'registerMessageListener'
                callbackContext.sendPluginResult(pluginResult);
            }
        };
        return listener;
    }

    protected void registerWearableMessageListener(Activity context) {
        Log.i(TAG, "registerMessageListener");
        Wearable.getDataClient(context).addListener(new DataClient.OnDataChangedListener() {
            @Override
            public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
                for(DataEvent dataEvent : dataEventBuffer) {
                    DataItem dataItem = dataEvent.getDataItem();
                    if(MSG_PATH.equals(dataItem.getUri().getPath())) {
                        String msg = new String(dataItem.getData(), StandardCharsets.UTF_8);
                        Log.d(TAG, "Message received: " + msg);
                        // This is the listener that created on the 'registerMessageListener'
                        listener.messageReceived(msg);
                    }
                }
            }
        });
    }

}
