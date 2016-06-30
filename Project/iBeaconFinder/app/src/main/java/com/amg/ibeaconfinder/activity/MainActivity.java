package com.amg.ibeaconfinder.activity;

import android.app.AlertDialog;
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
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.amg.ibeaconfinder.R;
import com.amg.ibeaconfinder.adapter.BeaconAdapter;
import com.amg.ibeaconfinder.adapter.BeaconListAdapter;
import com.amg.ibeaconfinder.model.Beacon;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.UUID;
import java.lang.Math;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "MainActivity";
    private static final int SCAN_INTERVAL_MS = 10000;
    boolean isScanning = false;
    private Handler scanHandler;

    Timer timer = new Timer();

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btLeScanner;

    ScanCallback scanCallback;
    BeaconListAdapter beaconAdapter;

    ArrayList<Beacon> beaconList;
    ListView listView;

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

        listView = (ListView) findViewById(R.id.cardList);
        beaconAdapter = new BeaconListAdapter(beaconList, this);
        listView.setAdapter(beaconAdapter);

        // SEARCH BUTTON
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabClick);

        // HANDLER
        scanHandler = new Handler();

        // SCAN CALLBACK AND SCANNER
        createScanner();
        setScanCallback();
    }

    void createScanner(){
        // BLUETOOTH
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        checkBluetoothState();
    }

    private void checkBluetoothState(){
        if (btAdapter == null)
            Snackbar.make(findViewById(R.id.coordinatorLayout), "Seu dispositivo não suporta Bluetooth!", Snackbar.LENGTH_LONG).show();
        else if (!btAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }

        if(btAdapter.isEnabled()) btLeScanner = btAdapter.getBluetoothLeScanner();
        else checkBluetoothState();
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
                        Log.i(TAG, "stopping scan");

                        Beacon beacon1 = beaconList.get(0);
                        Beacon beacon2 = beaconList.get(1);
                        double l1 = beacon1.getRealDistance();
                        double l2 = beacon2.getRealDistance();
                        double y = yComponent(l1,l2,10);
                        double x = xComponent(l1,y);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setTitle("Jogador").setMessage("X = " + x + "Y = " + y).create().show();
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
                   byte[] beaconData = scanRecord.getBytes(); //GETTING BEACON PDU
                   String iBeaconFirstByte = Integer.toHexString(beaconData[4] & 0xFF); // iBeacon First Byte
                   String iBeaconSecondByte = Integer.toHexString(beaconData[5] & 0xFF); // iBeacon Second Byte

                   if(iBeaconFirstByte.equals("2") && iBeaconSecondByte.equals("15")){
                       byte[] uuidBytes = new byte[16]; // UUID ARRAY
                       System.arraycopy(beaconData, 6, uuidBytes, 0, 16); // COPYING UUID BYTES
                       String uuid = getGuidFromByteArray(uuidBytes);

                       int major = twoBytesToShort(beaconData[22], beaconData[23]);
                       int minor = twoBytesToShort(beaconData[24], beaconData[25]);
                       int txPower = beaconData[26]&0xff;
                       double rssi = result.getRssi();

                       Beacon beacon = new Beacon();
                       double distance = beacon.calculateAccuracy(txPower, rssi);
                       String mac = result.getDevice().getAddress();
                       beacon.setUuid(uuid);
                       beacon.setMajor(Integer.toString(major));
                       beacon.setMinor(Integer.toString(minor));
                       beacon.setRssi(Integer.toString(result.getRssi()));
                       beacon.setMacAddress(mac);
                       beacon.setDistance(String.valueOf(round(distance, 4)) + "m");
                       beacon.setRealDistance(distance);

                       boolean found = false;
                       for(int i=0; i<beaconList.size(); i++){
                           if(beaconList.get(i).getMacAddress().equals(mac)){
                               beaconList.remove(i);
                               beaconList.add(i, beacon);
                               beaconAdapter.notifyDataSetChanged();
                               found = true;
                               break;
                           }
                       }

                       if(!found) beaconList.add(beacon);
                       beaconAdapter.notifyDataSetChanged();
                   }
               }
           }

           @Override
           public void onScanFailed(int errorCode) {
               Log.e(TAG, "onScanFailed errorCode " + errorCode);
           }
       };
   }

    public double yComponent(double l1, double l2, double base){
        double s = (base+l1+l2)/2;
        return (Math.sqrt(s*(s-l1)*(s-l2)*(s-base))*2)/base;
    } //l1 = rx1, distancia do beacon ate a base 0,0
      //l2 = rx2, distancia do beacon ate a base 10,0
      //base = distancia entre as bases

    public double xComponent(double l1, double l2){
        return Math.sqrt(Math.pow(l1, 2) - Math.pow(l2, 2));
    } //l1 = hipotenusa (Rx1) e l2 = componente y do ponto (altura)

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
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
