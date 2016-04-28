package com.amg.ibeaconfinder.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amg.ibeaconfinder.R;
import com.amg.ibeaconfinder.adapter.BeaconAdapter;
import com.amg.ibeaconfinder.model.Beacon;
import com.amg.ibeaconfinder.util.BeaconNotification;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "MainActivity";
    private static final int SCAN_INTERVAL_MS = 10000;
    boolean isScanning = false;
    private Handler scanHandler;

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btLeScanner;

    ScanCallback scanCallback;
    BeaconAdapter beaconAdapter;

    ArrayList<Beacon> BeaconList;

    private static final ScanSettings SCAN_SETTINGS =
            new ScanSettings.Builder().
                    setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setReportDelay(0)
                    .build();

    private static final ScanFilter SCAN_FILTER = new ScanFilter.Builder()
            .build();

    private static final List<ScanFilter> SCAN_FILTERS = buildScanFilters();

    private static List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();
        scanFilters.add(SCAN_FILTER);
        return scanFilters;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setLogo(R.mipmap.ic_launcher);

        // BEACON LIST;
        BeaconList = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        beaconAdapter = new BeaconAdapter(BeaconList);
        recyclerView.setAdapter(beaconAdapter);

        // SEARCH BUTTON
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabClick);

        // HANDLER
        scanHandler = new Handler();

        // SCAN CALLBACK AND SCANNER
        setScanCallback();
        createScanner();
    }

    void createScanner(){
        // BLUETOOTH
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        checkBluetoothState();
        btLeScanner = btAdapter.getBluetoothLeScanner();
    }

    private void checkBluetoothState(){
        if (btAdapter == null)
            Snackbar.make(findViewById(R.id.coordinatorLayout), "Seu dispositivo não suporta Bluetooth!", Snackbar.LENGTH_LONG).show();
        else if (!btAdapter.isEnabled() || btAdapter == null){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
    }

    View.OnClickListener fabClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Snackbar.make(findViewById(R.id.coordinatorLayout), "Buscando beacons...", Snackbar.LENGTH_LONG).show();
            if(!isScanning){
                btLeScanner.startScan(SCAN_FILTERS, SCAN_SETTINGS, scanCallback);
                isScanning = true;

                scanHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isScanning = false;
                        btLeScanner.stopScan(scanCallback);
                        Log.i(TAG, "stoping scan");
                    }
                }, SCAN_INTERVAL_MS);
            }
        }
    };


   private void setScanCallback() {
       scanCallback = new ScanCallback() {
           @Override
           public void onScanResult(int callbackType, ScanResult result) {
               Log.i("MainActivity", "Callback: Success");
               ScanRecord scanRecord = result.getScanRecord();
               if (scanRecord == null) {
                   Log.w(TAG, "Null ScanRecord for device " + result.getDevice().getAddress());
                   return;
               }
               else{
/*                   byte[] manufacturerData = scanRecord.getManufacturerSpecificData(76); //GETING BEACON PDU
                   byte[] uuidBytes = new byte[8]; // UUID ARRAY
                   System.arraycopy(manufacturerData, 0, uuidBytes, 0,8); // COPYING UUID BYTES
                   char[] data = new char[8];
                   StringBuilder sb = new StringBuilder();
                   for(int i=0; i<7; i++){
                       data[i] = (char) uuidBytes[i];
                       sb.append(data[i]);
                   }
                   String dataString = sb.toString();
                   Log.i("MainActivity", dataString);*/

                   //byte[] uuidBytes = new byte[8]; // UUID ARRAY
                   //System.arraycopy(manufacturerData, 0, uuidBytes, 0, 8); // COPYING UUID BYTES
                   //String hexString = bytesToHex(uuidBytes); // CREATING UUID STRING
                   //SEPARATING UUID STRING
/*                   String uuid =  hexString.substring(0,8) + "-" +
                           hexString.substring(8,12) + "-" +
                           hexString.substring(12,16) + "-" +
                           hexString.substring(16,20) + "-" +
                           hexString.substring(20,32);*/

                   // MAJOR
//                   final int major = (manufacturerData[20] & 0xff) * 0x100 + (manufacturerData[21] & 0xff);

                   // MINOR
//                   final int minor = (manufacturerData[22] & 0xff) * 0x100 + (manufacturerData[23] & 0xff);

                   //----------------------------------------------------------------------------------------------//



                   Beacon beacon = new Beacon();
                   beacon.setUuid("Name: " + scanRecord.getDeviceName());
                   beacon.setMajor("Address: " + result.getDevice().getAddress());
                   beacon.setMinor("UUID??? : ");
                   beacon.setRssi("RSSI: " + result.getRssi());
                   beacon.setDistance("distancia");

                   //Log.i("MainActivity","UUID: " +uuid + "\\nmajor: " +major +"\\nminor" +minor);

                   BeaconList.add(beacon);
                   beaconAdapter.notifyDataSetChanged();
               }


               // We're only interested in the UID frame time since we need the beacon ID to register.
/*                if (serviceData[0] != EDDYSTONE_UID_FRAME_TYPE) {
                    return;
                }*/

               // Extract the beacon ID from the service data. Offset 0 is the frame type, 1 is the
               // Tx power, and the next 16 are the ID.
               // See https://github.com/google/eddystone/eddystone-uid for more information.
/*                byte[] id = Arrays.copyOfRange(serviceData, 2, 18);
                if (arrayListContainsId(arrayList, id)) {
                    return;
                }*/

               // Draw it immediately and kick off a async request to fetch the registration status,
               // redrawing when the server returns.
/*                Log.i(TAG, "id " + Utils.toHexString(id) + ", rssi " + result.getRssi());

                Beacon beacon = new Beacon("EDDYSTONE", id, Beacon.STATUS_UNSPECIFIED, result.getRssi());
                insertIntoListAndFetchStatus(beacon);*/
           }

           @Override
           public void onScanFailed(int errorCode) {
               Log.e(TAG, "onScanFailed errorCode " + errorCode);
           }
       };
   }

   static final char[] hexArray = "0123456789ABCDEF".toCharArray();
   private static String bytesToHex(byte[] bytes)
   {
       char[] hexChars = new char[bytes.length * 2];
       for ( int j = 0; j < bytes.length; j++ )
       {
           int v = bytes[j] & 0xFF;
           hexChars[j * 2] = hexArray[v >>> 4];
           hexChars[j * 2 + 1] = hexArray[v & 0x0F];
       }
       return new String(hexChars);
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
