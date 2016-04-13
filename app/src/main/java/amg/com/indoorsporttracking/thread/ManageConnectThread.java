package amg.com.indoorsporttracking.thread;

import android.bluetooth.BluetoothSocket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ManageConnectThread extends Thread{

    private BluetoothSocket bTSocket;

    public ManageConnectThread() {}

    public void sendData(BluetoothSocket socket, int data) throws IOException{

        ByteArrayOutputStream output = new ByteArrayOutputStream(10);
        output.write(data);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(output.toByteArray());
    }

    public int receiveData(BluetoothSocket socket) throws IOException{

        byte[] buffer = new byte[10];
        ByteArrayInputStream input = new ByteArrayInputStream(buffer);
        InputStream inputStream = new ByteArrayInputStream(buffer);
        inputStream.read(buffer);
        return input.read();
    }
}
