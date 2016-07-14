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
    private int res[] = {R.color.CornflowerBlue, R.color.Crimson, R.color. Gold, R.color.SpringGreen,
                         R.color.DimGray, R.color.MediumOrchid, R.color.Black, R.color.SaddleBrown};

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
        viewHolder.color.setBackground(drawCircle(player.getColorpos()));
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

    Drawable drawCircle(int pos){
        Bitmap bmp = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        Paint p = new Paint();
        p.setColor(res[pos]);
        c.drawCircle(0, 0, 48, p);
        return new BitmapDrawable(context.getResources(), bmp);
    }
}
