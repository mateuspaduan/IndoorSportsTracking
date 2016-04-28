package com.amg.ibeaconfinder.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amg.ibeaconfinder.R;
import com.amg.ibeaconfinder.model.Beacon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 24/04/2016.
 */
public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.BeaconViewHolder>{
    private ArrayList<Beacon> beaconList;

    public BeaconAdapter(ArrayList<Beacon> beaconList){
        this.beaconList = beaconList;
    }

    @Override
    public BeaconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beacon_list, parent, false);
        return new BeaconViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BeaconViewHolder holder, int position) {
        Beacon beacon = beaconList.get(position);
        holder.uuid.setText("UUID: " + beacon.getUuid());
        holder.minor.setText("Minor: " + beacon.getMinor());
        holder.major.setText("Major: " + beacon.getMajor());
        holder.rssi.setText("RRSI: " + beacon.getRssi());
        holder.distance.setText("Distance: " + beacon.getDistance());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class BeaconViewHolder extends RecyclerView.ViewHolder{
        protected TextView uuid;
        protected TextView minor;
        protected TextView major;
        protected TextView rssi;
        protected TextView distance;

        public BeaconViewHolder(View view){
            super(view);
            this.uuid = (TextView) view.findViewById(R.id.uuid);
            this.minor = (TextView) view.findViewById(R.id.minor);
            this.major = (TextView) view.findViewById(R.id.major);
            this.rssi = (TextView) view.findViewById(R.id.rssi);
            this.distance = (TextView) view.findViewById(R.id.distance);
        }
    }
}
