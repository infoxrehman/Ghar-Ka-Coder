package com.lixtanetwork.gharkacoder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SwipeAdapter extends BaseAdapter {

    private Context mContext;
    private List<ModelFeeds> feedItemList;

    public SwipeAdapter(Context mContext, List<ModelFeeds> list) {
        this.mContext = mContext;
        this.feedItemList = list;
    }

    @Override
    public int getCount() {
        return feedItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return feedItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.feed_item_card, parent, false);

        TextView fullNameTv = view.findViewById(R.id.fullNameTv);
        TextView userNameTv = view.findViewById(R.id.userNameTv);
        TextView postTextTv = view.findViewById(R.id.postTextTv);
        TextView dateTv = view.findViewById(R.id.dateTv);
        ImageView feedImageIv = view.findViewById(R.id.feedImage);
        ImageView profileImage = view.findViewById(R.id.profileIv);

        ModelFeeds item = feedItemList.get(position);

        String userName = item.getUserName();
        String fullName = item.getName();
        String feedContent = item.getFeedContent();
        String profilePic = item.getProfilePic();
        String feedImage = item.getFeedImage();
        long feedDate = item.getFeedDate();

        Date timestamp = new Date(feedDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault());
        String formattedDate = dateFormat.format(timestamp);

        Glide.with(mContext).load(profilePic).into(profileImage);
        Glide.with(mContext).load(feedImage).into(feedImageIv);

        fullNameTv.setText(fullName);
        userNameTv.setText(userName);
        postTextTv.setText(feedContent);
        dateTv.setText(formattedDate);

        return view;
    }
}
