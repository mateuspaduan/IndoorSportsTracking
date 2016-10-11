package com.amg.ibeaconfinder.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amg.ibeaconfinder.R;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.key;
import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Lucas on 10/10/2016.
 */

public class WifiDialog extends DialogFragment {
    ListView WifiList;
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface MDialogListener {
        public void onMDialogPositiveClick(String newValue);
    }

    // Use this instance of the interface to deliver action events
    MDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View modifyView = inflater.inflate(R.layout.wifi_list, null);
        WifiList = (ListView) modifyView.findViewById(R.id.wifi_list);
        builder.setView(modifyView);
        builder.setNegativeButton("FECHAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(WIFI_SERVICE);
        List<ScanResult> apList = wifiManager.getScanResults();
        final ArrayList<String> ssidList = new ArrayList<>();
        for(ScanResult ap : apList){
            ssidList.add(ap.SSID);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_list_item_1, ssidList);
        WifiList.setAdapter(arrayAdapter);
        WifiList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WifiConfiguration wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = String.format("\"%s\"", ssidList.get(position));
                wifiConfig.preSharedKey = String.format("\"%s\"", "ist12345");

                WifiManager wifiManager = (WifiManager) getActivity().getSystemService(WIFI_SERVICE);
                //remember id
                int netId = wifiManager.addNetwork(wifiConfig);
                wifiManager.disconnect();
                wifiManager.enableNetwork(netId, true);
                wifiManager.reconnect();
            }
        });

    }
}
