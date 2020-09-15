package com.adhiwie.moodjournal.sensor.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.adhiwie.moodjournal.LinkedTasks;
import com.adhiwie.moodjournal.communication.helper.CommunicationMgr;
import com.adhiwie.moodjournal.utils.Log;

import java.util.Arrays;
import java.util.List;

public class NetworkStateSensor extends BroadcastReceiver {

    private String ssid = "";
    private String[] trainsWiFi = {"VirginTrainsFreeWiFi","CrossCountryWiFi","megabus-wifi","Loop WiFi",
            "Loop On Train WiFi","virgintrainswifi","TPE Wi-Fi","National Express Coach"};

    @Override
    public void onReceive(Context context, Intent intent) {
        Log log = new Log();
        try {
            //Transmit data if required
            new CommunicationMgr(context).transmissionDataIfRequired();
            new LinkedTasks(context).checkQuestionnaires();

//            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
//                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService (context.WIFI_SERVICE);
//                WifiInfo wifiInfo = wifiManager.getConnectionInfo ();
//                if (findSSIDForWifiInfo(wifiManager, wifiInfo) != null) {
//                    ssid = findSSIDForWifiInfo(wifiManager, wifiInfo);
//                    new Log().d("WiFi :"+ssid);
//
//                    if (Arrays.asList(trainsWiFi).contains(ssid)) {
//                        try {
//                            new LinkedTasks(context).checkQuestionnaires();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
        } catch (Exception e) {
            log.e(e.toString());
        }
    }

    public String findSSIDForWifiInfo(WifiManager manager, WifiInfo wifiInfo) {

        List<WifiConfiguration> listOfConfigurations = manager.getConfiguredNetworks();

        for (int index = 0; index < listOfConfigurations.size(); index++) {
            WifiConfiguration configuration = listOfConfigurations.get(index);
            if (configuration.networkId == wifiInfo.getNetworkId()) {
                return configuration.SSID;
            }
        }

        return null;
    }

}