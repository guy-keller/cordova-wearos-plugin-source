package com.github.guikeller.cordova.wearos;

public enum CordovaWearOsPluginAction {

    INIT("init"),
    SHUTDOWN("shutdown"),
    SEND_MESSAGE("sendMessage"),
    REGISTER_MESSAGE_LISTENER("registerMessageListener");

    private String value;

    private CordovaWearOsPluginAction(String value){
        this.value = value;
    }

    public String value(){
        return this.value;
    }

    public static CordovaWearOsPluginAction fromValue(String value){
        CordovaWearOsPluginAction action = null;
        CordovaWearOsPluginAction[] cordovaPluginActions = CordovaWearOsPluginAction.values();
        for (CordovaWearOsPluginAction cordovaPluginAction : cordovaPluginActions){
            if(cordovaPluginAction.value().equals(value)){
                action = cordovaPluginAction;
            }
        }
        return action;
    }

}
