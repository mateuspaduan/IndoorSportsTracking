package com.amg.livestatistcs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amg.livestatistcs.R;
import com.amg.livestatistcs.model.Player;

import java.util.ArrayList;

/**
 * Created by Lucas on 15/07/2016.
 */
public class SettingsListAdapter extends BaseAdapter{
    private final Integer[] res = {R.drawable.brown_400, R.drawable.dpurple_300, R.drawable.gray_500,
            R.drawable.green_a400, R.drawable.indigo_500, R.drawable.lblue_a100,
            R.drawable.lgreen_a200, R.drawable.orange_800, R.drawable.pink_300,
            R.drawable.red_500, R.drawable.teal_a200, R.drawable.yellow_500};

    private ArrayList<Player> playerList;
    private Context context;
    private LayoutInflater layoutInflater;

    public SettingsListAdapter(ArrayList<Player> playerList, Context context){
        this.playerList = playerList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return playerList.size();
    }

    @Override
    public Player getItem(int position) {
        return playerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        PlayerViewHolder viewHolder = null;

        if(view == null){
            view = layoutInflater.inflate(R.layout.settings_list, parent, false);
            viewHolder = new PlayerViewHolder();

            viewHolder.color = (Spinner) view.findViewById(R.id.color_spinner);
            viewHolder.name = (EditText) view.findViewById(R.id.name);
            viewHolder.mac = (TextView) view.findViewById(R.id.mac_tv);
            viewHolder.number = (TextView) view.findViewById(R.id.number_tv);
            viewHolder.mac_et = (AutoCompleteTextView) view.findViewById(R.id.mac_et);
            viewHolder.number_et = (EditText) view.findViewById(R.id.number_et);

            view.setTag(viewHolder);
        }
        else{
            viewHolder = (PlayerViewHolder) view.getTag();
        }

        Player player = playerList.get(position);
        SpinnerColorsAdapter adapter = new SpinnerColorsAdapter(context, res);
        viewHolder.color.setAdapter(adapter);
        viewHolder.color.setSelection(player.getColorpos());
        viewHolder.name.setHint(player.getName());
        viewHolder.number_et.setHint(player.getNumber());
        viewHolder.mac_et.setHint(player.getMac());

        return view;
    }

    public static class PlayerViewHolder {
        protected Spinner color;
        protected TextView mac;
        protected TextView number;
        protected EditText name;
        protected AutoCompleteTextView mac_et;
        protected EditText number_et;
    }
}
