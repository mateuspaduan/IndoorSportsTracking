package amg.com.indoorsporttracking.Adapter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import amg.com.indoorsporttracking.R;
import amg.com.indoorsporttracking.model.DeviceItem;

/**
 * Created by lucas on 06/04/2016.
 */
public class DeviceListAdapter extends ArrayAdapter<DeviceItem>{
    private Context context;
    private BluetoothAdapter bluetoothAdapter;

    public DeviceListAdapter(Context context, List items, BluetoothAdapter bluetoothAdapter){
        super(context, android.R.layout.simple_list_item_1, items);
        this.bluetoothAdapter = bluetoothAdapter;
        this.context = context;
    }

    private class ViewHolder{
        TextView titleText;
    }

    public View getView(int pos, View convertView, ViewGroup parent){
        ViewHolder holder = null;
        View line = null;
        DeviceItem item = (DeviceItem) getItem(pos);
        final String name = item.getDeviceName();
        TextView macAddress = null;
        View viewToUse = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        viewToUse = mInflater.inflate(R.layout.device_item_list, null);
        holder = new ViewHolder();
        holder.titleText = (TextView) viewToUse.findViewById(R.id.titleTextView);
        viewToUse.setTag(holder);

        macAddress = (TextView) macAddress.findViewById(R.id.macAddress);
        line = (View) line.findViewById(R.id.line);
        holder.titleText.setText(item.getDeviceName());
        macAddress.setText(item.getAddress());

        if(item.getDeviceName().toString() == "No Devices"){
            macAddress.setVisibility(View.INVISIBLE);
            line.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    (int) RelativeLayout.LayoutParams.WRAP_CONTENT, (int) RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            holder.titleText.setLayoutParams(params);
        }

        return viewToUse;
    }
}
