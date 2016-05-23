package com.amg.ibeaconfinder.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amg.ibeaconfinder.R;
import com.amg.ibeaconfinder.model.Beacon;

import java.util.ArrayList;

/**
 * Created by Lucas on 16/05/2016.
 */
public class BeaconListAdapter extends BaseAdapter{
    private ArrayList<Beacon> beaconList;
    private Context context;
    private LayoutInflater layoutInflater;

    public BeaconListAdapter(ArrayList<Beacon> beaconList, Context context){
        this.beaconList = beaconList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return beaconList.size();
    }

    @Override
    public Object getItem(int position) {
        return beaconList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        BeaconViewHolder viewHolder = null;

        if(view == null){
            view = layoutInflater.inflate(R.layout.beacon_list, parent, false);
            viewHolder = new BeaconViewHolder();

            viewHolder.uuid = (TextView) view.findViewById(R.id.uuid);
            viewHolder.minor = (TextView) view.findViewById(R.id.minor);
            viewHolder.major = (TextView) view.findViewById(R.id.major);
            viewHolder.rssi = (TextView) view.findViewById(R.id.rssi);
            viewHolder.distance = (TextView) view.findViewById(R.id.distance);
            viewHolder.mac = (TextView) view.findViewById(R.id.mac);

            view.setTag(viewHolder);
        }
        else{
            viewHolder = (BeaconViewHolder) view.getTag();
        }

        Beacon beacon = beaconList.get(position);
        viewHolder.uuid.setText("UUID: " + beacon.getUuid());
        viewHolder.minor.setText("Minor: " + beacon.getMinor());
        viewHolder.major.setText("Major: " + beacon.getMajor());
        viewHolder.rssi.setText("RRSI: " + beacon.getRssi());
        viewHolder.distance.setText("Distance: " + beacon.getDistance());
        viewHolder.mac.setText("MAC Address: " + beacon.getMacAddress());

        return view;
    }

    public static class BeaconViewHolder {
        protected TextView uuid;
        protected TextView minor;
        protected TextView major;
        protected TextView rssi;
        protected TextView distance;
        protected TextView mac;
    }
}
