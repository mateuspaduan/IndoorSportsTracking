package amg.com.indoorsporttracking.thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ServerConnectThread extends Thread {

    public ServerConnectThread() {}

    private BluetoothSocket bTSocket;

    public void acceptConnect(BluetoothAdapter bTAdapter, UUID mUUID){

        BluetoothServerSocket temp = null;
        try{
            temp = bTAdapter.listenUsingRfcommWithServiceRecord("Service_Name", mUUID);
        } catch(IOException e){
            Log.d("SERVERCONNECT", "Could not get a BLuetoothServerSocket: " + e.toString());
        }

        while(true){

            try{
                bTSocket = temp.accept();
            } catch(IOException e){
                Log.d("SERVERCONNECT", "Could not accept an incoming connection.");
                break;
            }
            if(bTSocket != null){
                try{
                    temp.close();
                } catch(IOException e){
                    Log.d("SERVERCONNECT", "Could not colse ServerSocket: " + e.toString());
                }
                break;
            }
        }
    }

    public void closeConnect(){

        try{
            bTSocket.close();
        } catch(IOException e){
            Log.d("SERVERCONNECT", "Could not close connection: " + e.toString());
        }
    }
}
