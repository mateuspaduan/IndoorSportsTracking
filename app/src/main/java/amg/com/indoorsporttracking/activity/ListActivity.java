package amg.com.indoorsporttracking.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Set;

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
    private Button searchDevices;
    private ArrayAdapter<String> pairedList, unknownList;
    private Set<BluetoothDevice> pairedDevices;
    private ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.list_activity);

        bluetoothState = (TextView) findViewById(R.id.bluetoothstate);

        searchDevices = (Button) findViewById(R.id.searchdevices);
        searchDevices.setOnClickListener(discoverButtonHandler);

        pairedDevicesList = (ListView) findViewById(R.id.paireddeviceslist);
        pairedList = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1);
        pairedDevicesList.setAdapter(pairedList);
        pairedDevicesList.setClickable(true);

        unknownDevicesList = (ListView) findViewById(R.id.unknowndeviceslist);
        unknownList = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1);
        unknownDevicesList.setAdapter(unknownList);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = bluetoothAdapter.getBondedDevices();

        CheckBluetoothState();
    }

    void CheckBluetoothState(){
        if(bluetoothAdapter == null) bluetoothState.setText("O dispositivo não suporta Bluetooth");
        else if(bluetoothAdapter.isEnabled()){
            bluetoothState.setText("BT habilitado");
            if(bluetoothAdapter.isDiscovering()) bluetoothState.setText("Buscando dispositivos");
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
                String content = device.getName() + " - " + device.getAddress();
                pairedList.add(content);
            }
        }
    }

    void  listUnknownDevices(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, filter);
    }

    View.OnClickListener discoverButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pairedList.clear();
            unknownList.clear();
            bluetoothAdapter.startDiscovery();
            listPairedDevices();
            listUnknownDevices();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ENABLE_BT){
            CheckBluetoothState();
        }
    }

    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            /*if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
                progressDialog = ProgressDialog.show(getApplicationContext(), "Aguarde", "Buscando dispositivos próximos...");
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                progressDialog.dismiss();*/
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                unknownList.add(device.getName() + " - " + device.getAddress());
                /*if(device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    unknownList.add(device.getName()+" - " + device.getAddress() + "\n");
                }*/
            }
        }
    };
}
