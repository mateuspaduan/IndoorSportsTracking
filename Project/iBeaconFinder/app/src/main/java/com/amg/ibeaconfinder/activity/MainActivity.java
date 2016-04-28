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

    List<Beacon> BeaconList;

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

        // BEACON LIST
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

        // SCAN CALLBACK
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                ScanRecord scanRecord = result.getScanRecord();
                if (scanRecord == null) {
                    Log.w(TAG, "Null ScanRecord for device " + result.getDevice().getAddress());
                    return;
                }
                byte[] manufacturerData = scanRecord.getManufacturerSpecificData(224);

                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();

                Beacon beacon = new Beacon();
                beacon.setUuid("uuid");
                beacon.setMajor("major");
                beacon.setMinor("minor");
                beacon.setRssi("rssi");
                beacon.setDistance("distancia");

                /*Log.i("MainActivity","UUID: " +uuid + "\\nmajor: " +major +"\\nminor" +minor);*/

                BeaconList.add(beacon);
                beaconAdapter.notifyDataSetChanged();

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
                Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        };
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


/*    private void setScanFilter(){
        ScanFilter.Builder mBuilder = new ScanFilter.Builder(); //Filtro
        ByteBuffer manufacturerData = ByteBuffer.allocate(24); //PDU BLE do Beacon
        ByteBuffer manufacturerDataMask = ByteBuffer.allocate(24); //Mascara p/ PDU
        byte[] uuid = getIdAsByte(UUID.fromString("0CF052C2-97CA-407C-84F8-B62AAC4E9020"));
        manufacturerData.put(0, (byte) 0x02);
        manufacturerData.put(1, (byte) 0x15);
        for(int i=2; i<18; i++) manufacturerData.put(i, uuid[i-2]);
        for(int i=0; i<18; i++) manufacturerDataMask.put((byte)0x01);
        mBuilder.setManufacturerData(224, manufacturerData.array(), manufacturerDataMask.array());
        scanFilter = mBuilder.build();
    }

    private void setScanSettings(){
        ScanSettings.Builder mBuilder = new ScanSettings.Builder();
        mBuilder.setReportDelay(0);
        mBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        scanSettings = mBuilder.build();
    }*/

 /*   private void setScanCallback(){
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
                ScanRecord scanRecord = result.getScanRecord();
                byte[] manufactuterData = scanRecord.getManufacturerSpecificData(224);
                int mRssi = result.getRssi();

                //if(manufactuterData[0] == (byte)0x02 && manufactuterData[1] == (byte)0x15){
                    Beacon beacon = new Beacon();
                    String data = manufactuterData.toString();

                    String uuid = data.substring(4, 35);
                    String major = data.substring(36, 39);
                    String minor = data.substring(40, 43);
                    String txPower = data.substring(44, 45);
                    String rssi = Integer.toString(mRssi);
                    String distance = Double.toString(beaconNotification.calculateAccuracy(Integer.parseInt(txPower), mRssi));

                    beaconNotification.notificate(Double.parseDouble(distance));

                    beacon.setUuid(uuid);
                    beacon.setMajor(major);
                    beacon.setMinor(minor);
                    beacon.setRssi(rssi);
                    beacon.setDistance(distance);

                    Log.i("MainActivity","UUID: " +uuid + "\\nmajor: " +major +"\\nminor" +minor);

                    BeaconList.add(beacon);
                    beaconAdapter.notifyDataSetChanged();
                }

                //TRABALHAR AQUI COM O BYTE PARA ENCONTRAR CADA DADO DO BEACON
               *//* The manufacturer specific data is extracted for a given company identifier,
                in this example 224 is the official bluetooth google company identifier.
                It is represented as an array of integers.
                All you need to do now is extract the first 2 bytes
                to make sure they match your protocol.
                The 16 following ones will be your UUID, the 2 next your major,
                2 more for your minor, and finally your tx power / reference RSSI.*//*
            //}

            @Override
            public void onScanFailed(int errorCode) {
                Toast.makeText(MainActivity.this, Integer.toString(errorCode), Toast.LENGTH_SHORT).show();
                super.onScanFailed(errorCode);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                Toast.makeText(MainActivity.this, "BATCH", Toast.LENGTH_SHORT).show();
                super.onBatchScanResults(results);
            }
        };

    }*/



/*    private byte[] getIdAsByte(UUID uuid)
    {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }*/


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
