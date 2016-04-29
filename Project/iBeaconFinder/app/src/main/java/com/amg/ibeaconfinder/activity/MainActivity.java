package com.amg.ibeaconfinder.activity;

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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.amg.ibeaconfinder.R;
import com.amg.ibeaconfinder.adapter.BeaconAdapter;
import com.amg.ibeaconfinder.model.Beacon;
import com.amg.ibeaconfinder.util.BeaconNotification;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "MainActivity";
    private static final int SCAN_INTERVAL_MS = 10000;
    boolean isScanning = false;
    private Handler scanHandler;

    BeaconNotification beaconNotification;

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btLeScanner;

    ScanCallback scanCallback;
    BeaconAdapter beaconAdapter;

    ArrayList<Beacon> beaconList;
    RecyclerView recyclerView;

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
        beaconList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        beaconAdapter = new BeaconAdapter(beaconList);
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
                   byte[] manufacturerData = scanRecord.getBytes(); //GETING BEACON PDU
                   byte[] uuidBytes = new byte[16]; // UUID ARRAY
                   System.arraycopy(manufacturerData, 6, uuidBytes, 0, 16); // COPYING UUID BYTES
                   String uuid = getGuidFromByteArray(uuidBytes);
                   int major = twoBytesToShort(manufacturerData[22], manufacturerData[23]);
                   int minor = twoBytesToShort(manufacturerData[24], manufacturerData[25]);
                   int txPower = manufacturerData[26]&0xff;
                   //double ssi = toDouble(manufacturerData[]);

                   //double distance = beaconNotification.calculateAccuracy(txPower, ssi);

                   Log.i("MainActivity", "UUID: " + uuid + "\\nmajor: " + major + "\\nminor" + minor + "\\ntxPower: " + txPower); // + "\\distance: " + distance);

                   Beacon beacon = new Beacon();
                   beacon.setUuid(uuid);
                   beacon.setMajor(Integer.toString(major));
                   beacon.setMinor(Integer.toString(minor));
                   beacon.setRssi(Integer.toString(result.getRssi()));
                   beacon.setDistance("Distância"); //String.valueOf(distance)

                   //beaconNotification.notificate(distance);

                   boolean found = false;
                   for(int i=0; i<beaconList.size(); i++){
                       if(beaconList.get(i).getUuid().equals(uuid)){
                           beaconList.add(i, beacon);
                           beaconAdapter.notifyItemRemoved(i);
                           found = true;
                           break;
                       }
                   }

                   if(!found) beaconList.add(beacon);
                   beaconAdapter.notifyDataSetChanged();
               }
           }

           @Override
           public void onScanFailed(int errorCode) {
               Log.e(TAG, "onScanFailed errorCode " + errorCode);
           }
       };
   }

    public static String getGuidFromByteArray(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        UUID uuid = new UUID(high, low);
        return uuid.toString();
    }

    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public static short twoBytesToShort(byte b1, byte b2) {
        return (short) ((b1 << 8) | (b2 & 0xFF));
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
