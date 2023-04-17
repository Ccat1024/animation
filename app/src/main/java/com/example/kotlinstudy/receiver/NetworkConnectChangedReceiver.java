package com.example.kotlinstudy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

public class NetworkConnectChangedReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle extras = intent.getExtras();
        Log.d("actioin", "onReceive: "+action);

        if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)){
            int wifiStates = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
            switch (wifiStates){
                case WifiManager.WIFI_STATE_DISABLED:
                    Log.d("wifiStates", "wifiStates: WIFI_STATE_DISABLED");break;
                case WifiManager.WIFI_STATE_DISABLING:
                    Log.d("wifiStates", "wifiStates: WIFI_STATE_DISABLING");break;
                case WifiManager.WIFI_STATE_ENABLED:
                    Log.d("wifiStates", "wifiStates: WIFI_STATE_ENABLED");break;
                case WifiManager.WIFI_STATE_ENABLING:
                    Log.d("wifiStates", "wifiStates: WIFI_STATE_ENABLING");break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    Log.d("wifiStates", "wifiStates: WIFI_STATE_UNKNOWN");break;

            }
        }

        if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)){
            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);

        }

    }
}
