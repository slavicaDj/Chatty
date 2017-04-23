package com.example.hpkorisnik.chatty.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hpkorisnik.chatty.object.Message;
import com.example.hpkorisnik.chatty.R;
import com.example.hpkorisnik.chatty.object.User;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;

public class CustomListAdapterChat extends BaseAdapter {

    private ArrayList<Message> listData;
    private Context mContext;
    private Activity activity;


    public CustomListAdapterChat(Activity activity, Context aContext, ArrayList<Message> listData) {

        this.listData = listData;
        this.mContext = aContext;
        this.activity = activity;

    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View customView = View.inflate(mContext, R.layout.listview_chat, null);


        listData.get(position);

        TextView textViewName = (TextView) customView.findViewById(R.id.textViewName);
        TextView textViewText = (TextView) customView.findViewById(R.id.textViewText);
        TextView textViewTime = (TextView) customView.findViewById(R.id.textViewTime);

        Message message = listData.get(position);
        boolean flag = true;

        String name =  message.getFromName();
        if (User.name.equals(message.getFromName())) {
            name = "You";
            flag = false;
        }

        textViewName.setText(name);
        textViewText.setText(message.getText());
        textViewTime.setText(getTimeFromUnixTime(Long.valueOf(message.getTimestamp())));

        if (flag) {
            textViewText.setBackgroundResource(R.color.chatBackground);
            textViewName.setBackgroundResource(R.color.chatBackground);
            textViewTime.setBackgroundResource(R.color.chatBackground);
        }

        return customView;
    }

    private static String getTimeFromUnixTime(long milliseconds ) {
        Date time = new Date(milliseconds*1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }
}