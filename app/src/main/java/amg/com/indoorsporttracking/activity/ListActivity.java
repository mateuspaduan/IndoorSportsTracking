package amg.com.indoorsporttracking.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

import amg.com.indoorsporttracking.R;
import amg.com.indoorsporttracking.thread.*;

public class ListActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;

    private ListView pairedDevicesList, unknownDevicesList;
    private TextView bluetoothState;
    private BluetoothAdapter bluetoothAdapter;
    private Button searchDevices;
    private ArrayAdapter<String> pairedAdapter, unknownAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayList<BluetoothDevice> pairedArrayList;
    //private ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.list_activity);

        StartBluetoothDiscover();
        CheckBluetoothState();
    }

    void CheckBluetoothState(){
        if(bluetoothAdapter == null) bluetoothState.setText("O dispositivo não suporta Bluetooth");
        else if(bluetoothAdapter.isEnabled()){
            bluetoothState.setText("BT habilitado");
            if(bluetoothAdapter.isDiscovering()){
                bluetoothState.setText("Buscando dispositivos");
                listUnknownDevices();
            }
        }
        else{
            bluetoothState.setText("Bluetooth não habilitado!");
            requestEnableBluetooth();
        }
    }

    void StartBluetoothDiscover(){
        bluetoothState = (TextView) findViewById(R.id.bluetoothstate);
        searchDevices = (Button) findViewById(R.id.searchdevices);
        searchDevices.setOnClickListener(discoverButtonHandler);

        pairedDevicesList = (ListView) findViewById(R.id.paireddeviceslist);
        pairedAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        pairedDevicesList.setAdapter(pairedAdapter);
        pairedDevicesList.setClickable(true);
        pairedDevicesList.setOnItemClickListener(pairedClickHandler);

        unknownDevicesList = (ListView) findViewById(R.id.unknowndeviceslist);
        unknownAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        unknownDevicesList.setAdapter(unknownAdapter);
        unknownDevicesList.setOnItemClickListener(unknownClickHandler);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = bluetoothAdapter.getBondedDevices();

        pairedArrayList = new ArrayList<>();
        pairedAdapter.clear();
        unknownAdapter.clear();
        bluetoothAdapter.startDiscovery();
        listPairedDevices();
        listUnknownDevices();
    }

    void requestEnableBluetooth(){
        Intent enableBTintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBTintent, REQUEST_ENABLE_BT);
    }

    void listPairedDevices(){
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
                String content = device.getName() + " - " + device.getAddress();
                pairedAdapter.add(content);
                pairedArrayList.add(device);
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

    AdapterView.OnItemClickListener pairedClickHandler = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ConnectThread makeConnection = new ConnectThread();
            ManageConnectThread manageConnectThread = new ManageConnectThread();
            BluetoothDevice device =  pairedArrayList.get(position);
            ParcelUuid[] parcelUuid = device.getUuids();
            makeConnection.connect(device, parcelUuid[position].getUuid());
            //manageConnectThread.receiveData(BluetoothSocket socket);
            makeConnection.close();
        }
    };

    AdapterView.OnItemClickListener unknownClickHandler = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            unknownDevicesList.getItemAtPosition(position);
        }
    };

    View.OnClickListener discoverButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //if(requestCode == REQUEST_ENABLE_BT){
            CheckBluetoothState();
        //}
    }

    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
                bluetoothState.setText("Buscando dispositivos");
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                bluetoothState.setText("Busca concluída");
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                unknownAdapter.add(device.getName() + " - " + device.getAddress());
            }
        }
    };
}
