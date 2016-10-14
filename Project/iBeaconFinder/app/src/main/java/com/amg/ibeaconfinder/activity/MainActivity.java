package com.amg.ibeaconfinder.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amg.ibeaconfinder.R;
import com.amg.ibeaconfinder.adapter.BeaconAdapter;
import com.amg.ibeaconfinder.alljoyn.AllJoynBusHandler;
import com.amg.ibeaconfinder.alljoyn.Constants;
import com.amg.ibeaconfinder.model.Beacon;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "MainActivity";
    private static final int INTERVAL = 1000;
    private Handler handler;
    private AllJoynBusHandler mBusHandler;
    public String currentSSID = "";
    private int time = 0;
    private Beacon beacon = null;

    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner btLeScanner;

    private ScanCallback scanCallback;
    private BeaconAdapter beaconAdapter;

    private ArrayList<Beacon> beaconList;
    private RecyclerView recyclerView;

    public ProgressDialog mDialog;

    private static final ScanSettings SCAN_SETTINGS =
            new ScanSettings.Builder().
                    setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
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

    static {
        System.loadLibrary("alljoyn_java");
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
        handler = new Handler();

        // SCAN CALLBACK AND SCANNER
        setScanCallback();

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null)
            Toast.makeText(this, "Seu dispositivo não suporta Bluetooth!", Toast.LENGTH_LONG);
        else if (!btAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }

        askPermissions();

        /* Make all AllJoyn calls through a separate handler thread to prevent blocking the UI. */
        HandlerThread busThread = new HandlerThread("BusHandler");
        busThread.start();
        mBusHandler = new AllJoynBusHandler(busThread.getLooper(), this, getPackageName(), mHandler);
    }

    private void askPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }


    View.OnClickListener fabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (btAdapter.isEnabled()) {
                Snackbar.make(findViewById(R.id.coordinatorLayout), "Buscando beacons...", Snackbar.LENGTH_INDEFINITE).show();
                btLeScanner = btAdapter.getBluetoothLeScanner();
                btLeScanner.startScan(SCAN_FILTERS, SCAN_SETTINGS, scanCallback);
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            registerReceiver(wifiReceiver, intentFilter);
            FragmentManager fragmentManager = getSupportFragmentManager();
            DialogFragment wifiDialog = new WifiDialog();
            wifiDialog.show(fragmentManager, "Main");
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
                } else {
                    byte[] manufacturerData = scanRecord.getBytes(); //GETTING BEACON PDU
                    byte[] uuidBytes = new byte[16]; // UUID ARRAY
                    System.arraycopy(manufacturerData, 6, uuidBytes, 0, 16); // COPYING UUID BYTES
                    String uuid = getGuidFromByteArray(uuidBytes);
                    int major = twoBytesToShort(manufacturerData[22], manufacturerData[23]);
                    int minor = twoBytesToShort(manufacturerData[24], manufacturerData[25]);
                    double txPower = -70;
                    double rssi = result.getRssi();

                    beacon = new Beacon();
                    double distance = beacon.calculateAccuracy(txPower, rssi);
                    beacon.setUuid(uuid);
                    beacon.setMajor(Integer.toString(major));
                    beacon.setMinor(Integer.toString(minor));
                    beacon.setRssi(Integer.toString(result.getRssi()));
                    beacon.setDistance(String.valueOf(round(distance, 4)) + "m");

                    boolean found = false;
                    for (int i = 0; i < beaconList.size(); i++) {
                        if (beaconList.get(i).getUuid().equals(uuid)) {
                            beaconList.add(i, beacon);
                            beaconAdapter.notifyItemRemoved(i);
                            found = true;
                            break;
                        }
                    }

                    if (!found) beaconList.add(beacon);
                    beaconAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e(TAG, "onScanFailed errorCode " + errorCode);
            }
        };
    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(ConnectivityManager.TYPE_WIFI == networkInfo.getType()){
                    if(networkInfo.isConnected()){
                        startAllJoinCommunication();
                    }
                }
            }
        }
    };

    private void startAllJoinCommunication() {
        WifiManager wifiManager = (WifiManager) getSystemService (Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();
        String ssid = info.getSSID();
        if(ssid.equals(currentSSID)){
            if(mDialog != null) mDialog.dismiss();
            time = 0;
            /* Connect to an AllJoyn object. */
            mBusHandler.sendEmptyMessage(Constants.CONNECT);
            mHandler.sendEmptyMessage(Constants.MESSAGE_START_PROGRESS_DIALOG);
            unregisterReceiver(wifiReceiver);
            startRepeatingTask.run();
        }
    }

    Runnable startRepeatingTask = new Runnable() {
        @Override
        public void run() {
            try{
                time++;
                new sendMessageAsync().execute();
            }finally {
                handler.postDelayed(startRepeatingTask, INTERVAL);
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_PING:
                    String ping = (String) msg.obj;
                    break;
                case Constants.MESSAGE_PING_REPLY:
                    String ret = (String) msg.obj;
                    break;
                case Constants.MESSAGE_POST_TOAST:
                    Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case Constants.MESSAGE_START_PROGRESS_DIALOG:
                    if(mDialog != null) mDialog.dismiss();
                    mDialog = ProgressDialog.show(MainActivity.this,
                            "",
                            "Finding Simple Service.\nPlease wait...",
                            true,
                            true);
                    break;
                case Constants.MESSAGE_STOP_PROGRESS_DIALOG:
                    mDialog.dismiss();
                    break;
                case Constants.FINISH:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    private class sendMessageAsync extends AsyncTask<Void, Void, Integer>{

        @Override
        protected Integer doInBackground(Void... params) {
            if(beacon != null){
                String beaconInfo = time + "," + beacon.getDistance() + "," + beacon.getUuid();
                Message msg = mBusHandler.obtainMessage(Constants.MESSAGE, beaconInfo);
                mBusHandler.sendMessage(msg);
            }
            return null;
        }
    }
    //---------------------------------------------------------------------------------------------

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
    @Override

    protected void onDestroy() {
        super.onDestroy();

        /* Disconnect to prevent resource leaks. */
        mBusHandler.sendEmptyMessage(Constants.DISCONNECT);
    }
}
