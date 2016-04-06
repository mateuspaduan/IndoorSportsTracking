package amg.com.indoorsporttracking.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

import amg.com.indoorsporttracking.Adapter.DeviceListAdapter;
import amg.com.indoorsporttracking.R;
import amg.com.indoorsporttracking.model.DeviceItem;

/**
 * Created by lucas on 06/04/2016.
 */
public class ListActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PAIRED_DEVICES = 2;

    private ListView pairedDevicesList, unknownDevicesList;
    private TextView bluetoothState;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList pairedList, unknownList;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayAdapter<DeviceItem> pairedAdapter, unknwonAdapter;


    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.list_activity);

        pairedDevicesList = (ListView) findViewById(R.id.paireddeviceslist);
        unknownDevicesList = (ListView) findViewById(R.id.unknowndeviceslist);
        bluetoothState = (TextView) findViewById(R.id.bluetoothstate);

        pairedList = new ArrayList<>();
        unknownList = new ArrayList<>();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        unknwonAdapter = new DeviceListAdapter(this, unknownList, bluetoothAdapter);
        pairedAdapter = new DeviceListAdapter(this, pairedList, bluetoothAdapter);

        unknownDevicesList.setAdapter(unknwonAdapter);
        pairedDevicesList.setAdapter(pairedAdapter);

        bluetoothAdapter.startDiscovery();
        CheckBluetoothState();
    }

    void CheckBluetoothState(){
        if(bluetoothAdapter == null) bluetoothState.setText("O dispositivo não suporta Bluetooth");
        else if(bluetoothAdapter.isEnabled()){
            if(bluetoothAdapter.isDiscovering()){
                bluetoothState.setText("Descobrindo dispositivos");
            }
            else{
                bluetoothState.setText("Pronto para listar dispositivos pareados");
                listPairedDevices();
                listUnkownDevices();
            }
        }
        else{
            bluetoothState.setText("Bluetooth não habilitado!");
            Intent enableBTintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTintent, REQUEST_ENABLE_BT);
        }
    }

    void listPairedDevices(){
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
                DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), "false");
                pairedList.add(newDevice);
            }
        }
    }

    void listUnkownDevices(){
        unknwonAdapter.clear();

        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_NAME);
                    DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), "false");
                    unknownList.add(newDevice);
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver, filter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ENABLE_BT) CheckBluetoothState();
        /*else if(requestCode == REQUEST_PAIRED_DEVICES) {

        }*/
    }

}
