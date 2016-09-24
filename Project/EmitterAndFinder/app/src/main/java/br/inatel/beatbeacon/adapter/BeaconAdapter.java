package br.inatel.beatbeacon.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.inatel.beatbeacon.R;
import br.inatel.beatbeacon.model.Beacon;

/**
 * Created by Lucas on 16/05/2016.
 */
public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.BeaconViewHolder>{
    private ArrayList<Beacon> beaconList;

    public BeaconAdapter(ArrayList<Beacon> beaconList){
        this.beaconList = beaconList;
    }

    @Override
    public BeaconAdapter.BeaconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.beacon_list, parent, false);
        return new BeaconViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BeaconViewHolder holder, int position) {
        Beacon beacon = beaconList.get(position);
        holder.mac.setText("MAC: " + beacon.getMac());
        holder.name.setText("Name: " + beacon.getDeviceName());
        holder.rssi.setText("RRSI: " + beacon.getRssi());
        holder.distance.setText("Distance: " + beacon.getDistance());

    }

    @Override
    public int getItemCount() {
        return beaconList.size();
    }

    public static class BeaconViewHolder extends RecyclerView.ViewHolder{
        protected TextView mac;
        protected TextView name;
        protected TextView rssi;
        protected TextView distance;

        public BeaconViewHolder(View view){
            super(view);
            mac = (TextView) view.findViewById(R.id.mac);
            name = (TextView) view.findViewById(R.id.devicename);
            rssi = (TextView) view.findViewById(R.id.rssi);
            distance = (TextView) view.findViewById(R.id.distance);
        }
    }
}
