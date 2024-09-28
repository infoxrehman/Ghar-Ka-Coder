package com.lixtanetwork.gharkacoder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class SwipeAdapter extends BaseAdapter {

    private Context mContext;
    private List<Integer> list;

    public SwipeAdapter(Context mContext, List<Integer> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item_card, parent, false);
        } else {
            view = convertView;
        }
        return view;
    }
}
