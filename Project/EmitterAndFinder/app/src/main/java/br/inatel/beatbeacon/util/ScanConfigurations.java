package br.inatel.beatbeacon.util;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.inatel.beatbeacon.activity.MainActivity;
import br.inatel.beatbeacon.adapter.BeaconAdapter;
import br.inatel.beatbeacon.model.Beacon;

/**
 * Created by lucas on 15/09/2016.
 */
public interface ScanConfigurations {
    //BEACON LIST
    ArrayList<Beacon> beaconList = new ArrayList<>();
    BeaconAdapter beaconAdapter = new BeaconAdapter(beaconList);

    //BLE SCAN SETTINGS
    ScanSettings SCAN_SETTINGS =
            new ScanSettings.Builder().
                    setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setReportDelay(0)
                    .build();

    //BLE SCAN FILTER
    List<ScanFilter> SCAN_FILTERS =
            new ArrayList<>(Collections.singletonList(
                new ScanFilter.Builder()
                    .build()));

    //BLE SCAN CALLBACK
    ScanCallback SCAN_CALLBACK = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("scanCallback", "Callback: Success");
            ScanRecord scanRecord = result.getScanRecord();
            if (scanRecord == null) {
                Log.w("scanCallback", "Null ScanRecord for device " + result.getDevice().getAddress());
            } else {
                double txPower = -70;
                double rssi = result.getRssi();

                Beacon beacon = new Beacon();
                beacon.setMac(result.getDevice().getAddress());
                beacon.setDeviceName(result.getDevice().getName());
                beacon.setRssi(Integer.toString(result.getRssi()));
                beacon.setDistance(Double.toString(beacon.calculateAccuracy(txPower, rssi)));
                beaconList.add(beacon);

                for (int i = 0; i < beaconList.size(); i++) {
                    if (beaconList.get(i).getMac().equals(beacon.getMac())) {
                        beaconList.remove(i);
                        beaconAdapter.notifyItemRemoved(i);
                        break;
                    }
                }

                beaconList.add(beacon);
                beaconAdapter.notifyDataSetChanged();
                Log.v("beaconsList", beaconList.toString());
            }
        }
        @Override
        public void onScanFailed(int errorCode) {
            Log.e("scanCallback", "onScanFailed errorCode " + errorCode);
        }
    };

    //public ArrayList<Beacon> getBeaconList() { return beaconList; }
    //public ScanSettings getScanSettings(){ return SCAN_SETTINGS; }
    //public List<ScanFilter> getScanFilters() { return SCAN_FILTERS; }
    //public ScanCallback getScanCallback() {return SCAN_CALLBACK; }
}
