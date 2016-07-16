package com.amg.ibeaconfinder.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import com.amg.ibeaconfinder.activity.MainActivity;



/**
 * Created by mateu on 7/16/2016.
 */
public class WiFiP2pBReceiver extends BroadcastReceiver{

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mainActivity;

    public WiFiP2pBReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity){

        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mainActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
                Toast.makeText(context, "WiFiP2p is supported", Toast.LENGTH_SHORT).show();
            else Toast.makeText(context, "WiFiP2p is not supported", Toast.LENGTH_SHORT).show();
        }


    }
}
