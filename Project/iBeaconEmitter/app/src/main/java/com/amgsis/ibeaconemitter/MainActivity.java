package com.amgsis.ibeaconemitter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    
    private AdvertiseData advertiseData;
    private AdvertiseSettings advertiseSettings;
    private AdversiteCallback advertiseCallback;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        //************** INSTANCIAR O ADVERTISER ****************
        
        //************** INSTANCIAR O ADVERTISER ****************

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothLeAdvertiser.startAdvertising(advertiseData, advertiseSettings, advertiseCallback);
            }
        });
        
        setAdvertiseData();
        setAdvertiseSettings();
        setAdvertiseCallback();
    }
    
    private void setAdvertiseData(){
        AdvertiseData.Builder mBuilder = new AdvertiseData.Builder();
        ByteBuffer manufacturerData = ByteBuffer.allocate(24);
        byte[] uuid = getIdAsByte(UUID.fromString("0CF052C2-97CA-407C-84F8-B62AAC4E9020"));
        manufacturerData.put(0, (byte) 0x02); //FIRST BYTE iBeacon
        manufacturerData.put(1, (byte) 0x15); //SECOND BYTE iBeacon
        for(int i=2; i<18; i++) manufacturerData.put(i, uuid[i-2]); //UUID
        manufacturerData.put(18, (byte) 0x00); //FIRST BYTE Major
        manufacturerData.put(19, (byte) 0x09); //SECOND BYTE Major
        manufacturerData.put(20, (byte) 0x00); //FIRST BYTE Minor
        manufacturerData.put(21, (byte) 0x06); //SECOND BYTE Minor
        manufacturerData.put(22, (byte) 0xB5); //Reference TX Power
        mBuilder.addManufacturerData(224, manufacturerData.array()); //Using Google ID
        advertiseData = mBuilder.build();
    }
    
    private void setAdvertiseSettings(){
        AdvertiseSettings.Builder mBuilder = new AdvertiseSettings.Builder();
        mBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
        mBuilder.setConnectable(false);
        mBuilder.setTimeout(0);
        mBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        advertiseSettings = mBuilder.build();
    }
    
    private void setAdvertiseCallback(){
        //IMPLEMENTAR METODOS OBRIGATORIOS
    }
    
    private byte[] getIdAsByte(UUID uuid)
    {
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
