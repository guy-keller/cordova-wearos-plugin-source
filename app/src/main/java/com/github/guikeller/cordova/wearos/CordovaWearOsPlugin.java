package com.github.guikeller.cordova.wearos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class CordovaWearOsPlugin extends CordovaPlugin {

    private static final String TAG = CordovaWearOsPlugin.class.getSimpleName();

    private WearOsServiceConnection serviceConnection;
    private CordovaInterface cordovaInterface;
    private CallbackContext callbackContext;
    private Intent intent;

    public CordovaWearOsPlugin(){
        super();
        Log.i(TAG, "constructor");
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Log.i(TAG, "initialize");
        super.initialize(cordova, webView);
        this.cordovaInterface = cordova;
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
        if (this.intent == null){
            Activity context = this.cordovaInterface.getActivity();
            this.intent = new Intent(context, WearOsListenerService.class);
            this.intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            // Start service then bind; so onCreate is invoked on the 'Service' class
            context.startService(this.intent);
            this.serviceConnection = new WearOsServiceConnection();
            context.bindService(this.intent, this.serviceConnection, Context.BIND_AUTO_CREATE);
        }
        callbackContext.success();
    }

    protected void shutdown(CallbackContext callbackContext) {
        Log.i(TAG,"shutdown");
        if (this.intent != null) {
            Activity context = this.cordovaInterface.getActivity();
            context.stopService(this.intent);
        }
        callbackContext.success();
    }

    protected void registerMessageListener(CallbackContext callbackContext) {
        Log.i(TAG,"registerMessageListener :: listener: "+callbackContext);
        this.callbackContext = callbackContext;
        if (this.serviceConnection != null && this.callbackContext == null) {
            WearOsMessageListener listener = createWearOsMessageListener();
            this.serviceConnection.getService().registerMessageListener(listener);
        }
        callbackContext.success();
    }

    protected void sendMessage(JSONArray args, CallbackContext callbackContext){
        try {
            Log.i(TAG,"sendMessage :: args: "+args);
            if (this.serviceConnection != null && args != null) {
                Activity context = this.cordovaInterface.getActivity();
                WearOsMessageSender sender = new WearOsMessageSender(context);
                String msg = args.getString(0);
                sender.sendMessage(msg);
                callbackContext.success();
            }
        } catch (Exception ex){
            callbackContext.error("Not able to send message: " + ex.getMessage());
        }
    }

    private WearOsMessageListener createWearOsMessageListener(){
        Log.i(TAG,"createWearOsMessageListener");
        WearOsMessageListener listener = new WearOsMessageListener() {
            @Override
            public void messageReceived(String msg) {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, msg);
                pluginResult.setKeepCallback(true);
                CordovaWearOsPlugin.this.callbackContext.sendPluginResult(pluginResult);
            }
        };
        return listener;
    }

}
