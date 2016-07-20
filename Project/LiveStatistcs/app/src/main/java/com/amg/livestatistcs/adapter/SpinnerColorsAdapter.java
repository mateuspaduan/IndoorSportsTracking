package com.amg.livestatistcs.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.amg.livestatistcs.R;

/**
 * Created by Lucas on 19/07/2016.
 */

public class SpinnerColorsAdapter extends ArrayAdapter<Integer> {
    private Integer[] res;

    public SpinnerColorsAdapter(Context context, Integer[] res) {
        super(context, android.R.layout.simple_spinner_item, res);
        this.res = res;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getImageForPosition(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getImageForPosition(position);
    }

    private View getImageForPosition(int position) {
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundResource(res[position]);
        imageView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return imageView;
    }
}
