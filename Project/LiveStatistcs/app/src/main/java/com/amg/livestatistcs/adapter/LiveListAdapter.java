package com.amg.livestatistcs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amg.livestatistcs.R;
import com.amg.livestatistcs.model.Player;

import java.util.ArrayList;

/**
 * Created by Lucas on 13/07/2016.
 */
public class LiveListAdapter extends BaseAdapter{
    private final int res[] = {R.drawable.brown_400, R.drawable.dpurple_300, R.drawable.gray_500,
            R.drawable.green_a400, R.drawable.indigo_500, R.drawable.lblue_a100,
            R.drawable.lgreen_a200, R.drawable.orange_800, R.drawable.pink_300,
            R.drawable.red_500, R.drawable.teal_a200, R.drawable.yellow_500};

    private ArrayList<Player> playerList;
    private Context context;
    private LayoutInflater layoutInflater;

    public LiveListAdapter(ArrayList<Player> playerList, Context context){
        this.playerList = playerList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return playerList.size();
    }

    @Override
    public Object getItem(int position) {
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
            view = layoutInflater.inflate(R.layout.live_list, parent, false);
            viewHolder = new PlayerViewHolder();

            viewHolder.color = (ImageView) view.findViewById(R.id.color);
            viewHolder.name_number = (TextView) view.findViewById(R.id.name_number);
            viewHolder.totalrun = (TextView) view.findViewById(R.id.totalrun);
            viewHolder.avgrun = (TextView) view.findViewById(R.id.avgrun);

            view.setTag(viewHolder);
        }
        else{
            viewHolder = (PlayerViewHolder) view.getTag();
        }

        Player player = playerList.get(position);
        viewHolder.color.setBackgroundResource(res[player.getColorpos()]);
        viewHolder.name_number.setText(player.getName() + "(" + player.getNumber() + ")");
        viewHolder.totalrun.setText("Total: " + player.getTotalrun());
        viewHolder.avgrun.setText("Média: " + player.getAvgrun());

        return view;
    }

    public static class PlayerViewHolder {
        protected ImageView color;
        protected TextView name_number;
        protected TextView totalrun;
        protected TextView avgrun;
    }
}
