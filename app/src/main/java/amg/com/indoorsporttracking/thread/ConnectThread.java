package amg.com.indoorsporttracking.thread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread{

    private BluetoothSocket bTSocket;

    public boolean connect(BluetoothDevice bTDevice, UUID mUUID) {

        BluetoothSocket temp = null;
        try {
            temp = bTDevice.createRfcommSocketToServiceRecord(mUUID); //Obtém o RFCOMM
        } catch (IOException e) {
            Log.d("CONNECTTHREAD", "Could not create RFCOMM socket:" + e.toString());
            return false;
        }

        try {
            bTSocket.connect(); //Inicia a conexão pelo RFCOMM criado
        } catch(IOException e) {
            Log.d("CONNECTTHREAD","Could not connect: " + e.toString());
            try {
                bTSocket.close(); //Fecha para não gastar processamento sem utilizar a conexão
            } catch(IOException close) {
                Log.d("CONNECTTHREAD", "Could not close connection:" + e.toString());
                return false;
            }
        }
        return true;
    }

    public boolean close(){

        try{
            bTSocket.close();
        } catch(IOException e){
            Log.d("CONNECTTHREAD", "Could not close connection: " + e.toString());
            return false;
        }
        return true;
    }
}
