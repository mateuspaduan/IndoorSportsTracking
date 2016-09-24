package br.inatel.beatbeacon.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

import br.inatel.beatbeacon.R;
import br.inatel.beatbeacon.adapter.BeaconAdapter;
import br.inatel.beatbeacon.model.Beacon;
import br.inatel.beatbeacon.util.AdvertiserConfigurations;
import br.inatel.beatbeacon.util.ScanConfigurations;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = MainActivity.class.getName();

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    protected BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private BeaconAdapter mBeaconAdapter;
    private ArrayList<Beacon> mBeaconsList;
    private RecyclerView mReclyclerView;

    private FloatingActionButton fab;
    private boolean advertising = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // BEACON LIST;
        mBeaconsList = ScanConfigurations.beaconList;
        mReclyclerView = (RecyclerView) findViewById(R.id.cardList);
        mReclyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mReclyclerView.setLayoutManager(llm);
        mBeaconAdapter = ScanConfigurations.beaconAdapter;
        mReclyclerView.setAdapter(mBeaconAdapter);

        // ACTION BUTTON
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabClick);

        // ENABLING BT
        enableBluetooth();
        if(android.os.Build.VERSION.SDK_INT >= 23) askPermissions();
    }

    void enableBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
            Toast.makeText(this, "Seu dispositivo não suporta Bluetooth!", Toast.LENGTH_LONG);
        else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
    }

    private AdvertiseData setAdvertiseData() {
        AdvertiseData.Builder mBuilder = new AdvertiseData.Builder();
        byte[] uuid = getIdAsByte(UUID.fromString("0CF052C2-97CA-407C-84F8-B62AAC4E9020"));
        ByteBuffer manufacturerData = ByteBuffer.allocate(24);
        manufacturerData.put(0, (byte) 0x02); //FIRST BYTE iBeacon
        manufacturerData.put(1, (byte) 0x15); //SECOND BYTE iBeacon
        for (int i = 2; i < 18; i++) manufacturerData.put(i, uuid[i - 2]); //UUID
        manufacturerData.put(18, (byte) 0x00); //FIRST BYTE Major
        manufacturerData.put(19, (byte) 0x09); //SECOND BYTE Major
        manufacturerData.put(20, (byte) 0x00); //FIRST BYTE Minor
        manufacturerData.put(21, (byte) 0x06); //SECOND BYTE Minor
        manufacturerData.put(22, (byte) 0x46); //Reference TX Power
        mBuilder.addManufacturerData(224, manufacturerData.array());
        return mBuilder.build();
    }

    View.OnClickListener fabClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(advertising){
                advertising = false;
                //if(mBluetoothLeAdvertiser != null)
                 //   mBluetoothLeAdvertiser.stopAdvertising(AdvertiserConfigurations.ADVERTISE_CALLBACK);
                if (mBluetoothAdapter.isEnabled()) {
                    Snackbar.make(findViewById(R.id.coordinatorLayout), "Buscando beacons...", Snackbar.LENGTH_INDEFINITE).show();
                    mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                    mBluetoothLeScanner.startScan(
                            ScanConfigurations.SCAN_FILTERS,
                            ScanConfigurations.SCAN_SETTINGS,
                            ScanConfigurations.SCAN_CALLBACK);
                }

            } else{
                advertising = true;
                //if(mBluetoothLeScanner != null)
                //    mBluetoothLeScanner.stopScan(ScanConfigurations.SCAN_CALLBACK);
                if(mBluetoothAdapter.isMultipleAdvertisementSupported()){
                    if(mBluetoothAdapter.isEnabled()){
                        Snackbar.make(findViewById(R.id.coordinatorLayout), "Emitindo...", Snackbar.LENGTH_INDEFINITE).show();
                        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
                        mBluetoothLeAdvertiser.startAdvertising(
                                AdvertiserConfigurations.ADVERTISE_SETTINGS,
                                setAdvertiseData(),
                                AdvertiserConfigurations.ADVERTISE_CALLBACK);
                    }
                }
                else
                    Snackbar.make(findViewById(R.id.coordinatorLayout), "Seu dispositivo não suporta o Advertiser!", Snackbar.LENGTH_LONG).show();
            }
        }
    };

    private void askPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }

    private byte[] getIdAsByte(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
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
