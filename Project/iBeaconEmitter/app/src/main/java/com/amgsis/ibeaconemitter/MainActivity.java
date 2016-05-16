package com.amgsis.ibeaconemitter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Text;

import java.nio.ByteBuffer;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    private AdvertiseData advertiseData;
    private AdvertiseSettings advertiseSettings;
    private AdvertiseCallback advertiseCallback;
    protected BluetoothLeAdvertiser bluetoothLeAdvertiser;
    protected BluetoothAdapter bluetoothAdapter;

    private TextView uuid;
    private TextView minor;
    private TextView major;
    private TextView txPower;
    private TextView beaconId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INSTANCIANDO TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(R.string.app_name);

        // INSTANCIANDO BOTAO
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabClick);

        // INSTANCIANDO BLUETOOTH ADAPTER E ADVERTISER
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();

        // INSTANCIANDO TEXTVIEWS
        uuid = (TextView) findViewById(R.id.uuid);
        minor = (TextView) findViewById(R.id.minor);
        major = (TextView) findViewById(R.id.major);
        txPower = (TextView) findViewById(R.id.txPower);
        beaconId = (TextView) findViewById(R.id.beaconId);

        // CONFIGURANDO EMISOR
        checkBluetoothState();
        setAdvertiseData();
        setAdvertiseSettings();
        setAdvertiseCallback();
    }

    private void checkBluetoothState(){
        if (bluetoothAdapter == null)
            Snackbar.make(findViewById(R.id.coordinatorLayout), "Seu dispositivo não suporta Bluetooth!", Snackbar.LENGTH_LONG).show();
        else if (!bluetoothAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
    }

    private void setAdvertiseData() {
        AdvertiseData.Builder mBuilder = new AdvertiseData.Builder();
        ByteBuffer manufacturerData = ByteBuffer.allocate(24);
        byte[] uuid = getIdAsByte(UUID.fromString("0CF052C2-97CA-407C-84F8-B62AAC4E9020"));
        manufacturerData.put(0, (byte) 0x02); //FIRST BYTE iBeacon
        manufacturerData.put(1, (byte) 0x15); //SECOND BYTE iBeacon
        for (int i = 2; i < 18; i++) manufacturerData.put(i, uuid[i - 2]); //UUID
        manufacturerData.put(18, (byte) 0x00); //FIRST BYTE Major
        manufacturerData.put(19, (byte) 0x09); //SECOND BYTE Major
        manufacturerData.put(20, (byte) 0x00); //FIRST BYTE Minor
        manufacturerData.put(21, (byte) 0x06); //SECOND BYTE Minor
        manufacturerData.put(22, (byte) 0x46); //Reference TX Power
        mBuilder.addManufacturerData(224, manufacturerData.array()); //Using Google ID
        advertiseData = mBuilder.build();
    }

    private void setAdvertiseSettings() {
        AdvertiseSettings.Builder mBuilder = new AdvertiseSettings.Builder();
        mBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
        mBuilder.setConnectable(false);
        mBuilder.setTimeout(0);
        mBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        advertiseSettings = mBuilder.build();
    }

    private void setAdvertiseCallback() {
        advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                uuid.setText("UUID: 0CF052C2-97CA-407C-84F8-B62AAC4E9020");
                minor.setText("Minor: 6");
                major.setText("Major: 9");
                beaconId.setText("Beacon: 0215 - iBeacon");
                txPower.setText("Reference Power: " + Integer.toString(0xB5));
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
            }
        };
    }

    View.OnClickListener fabClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(bluetoothAdapter.isMultipleAdvertisementSupported())
                bluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);
            else
                Snackbar.make(findViewById(R.id.coordinatorLayout), "Seu dispositivo não suporta o Advertiser!", Snackbar.LENGTH_LONG).show();
        }
    };

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
