package com.amg.ibeaconfinder.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.amg.ibeaconfinder.R;
import com.amg.ibeaconfinder.adapter.BeaconAdapter;
import com.amg.ibeaconfinder.model.Beacon;
import com.amg.ibeaconfinder.util.BeaconNotification;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner btLeScanner;
    private ScanFilter scanFilter;
    private ScanSettings scanSettings;
    private ScanCallback scanCallback;
    private BeaconNotification beaconNotification;
    List<Beacon> BeaconList;

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
        BeaconAdapter beaconAdapter = new BeaconAdapter(BeaconList);
        recyclerView.setAdapter(beaconAdapter);

        // SEARCH BUTTON
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabClick);

        // BEACON SCAN
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btLeScanner = btAdapter.getBluetoothLeScanner();
        checkBluetoothState();
        setScanFilter();
        setScanSettings();
        setScanCallback();
    }

    private void checkBluetoothState(){
        if (btAdapter == null)
            Snackbar.make(findViewById(R.id.coordinatorLayout), "Seu dispositivo não suporta Bluetooth!", Snackbar.LENGTH_LONG).show();
        else if (!btAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
    }

    private void setScanFilter(){
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
    }

    private void setScanCallback(){
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
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

                    BeaconList.add(beacon);
                }

                //TRABALHAR AQUI COM O BYTE PARA ENCONTRAR CADA DADO DO BEACON
               /* The manufacturer specific data is extracted for a given company identifier,
                in this example 224 is the official bluetooth google company identifier.
                It is represented as an array of integers.
                All you need to do now is extract the first 2 bytes
                to make sure they match your protocol.
                The 16 following ones will be your UUID, the 2 next your major,
                2 more for your minor, and finally your tx power / reference RSSI.*/
            //}

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }
        };

    }

    private byte[] getIdAsByte(UUID uuid)
    {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    View.OnClickListener fabClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Snackbar.make(findViewById(R.id.coordinatorLayout), "Buscando beacons...", Snackbar.LENGTH_LONG).show();
            btLeScanner.startScan(Arrays.asList(scanFilter), scanSettings, scanCallback);
        }
    };

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
