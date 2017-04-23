package com.example.hpkorisnik.chatty.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hpkorisnik.chatty.object.Message;
import com.example.hpkorisnik.chatty.R;
import com.example.hpkorisnik.chatty.object.User;
import com.example.hpkorisnik.chatty.activity.ChatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class CustomListAdapterConversations extends BaseAdapter {

    private ArrayList<Message> listData;
    private Context mContext;
    private Activity activity;

    private String name;
    private String userId;

    public CustomListAdapterConversations(Activity activity, Context aContext, ArrayList<Message> listData) {

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
    public View getView(final int position, View view, ViewGroup viewGroup) {

        View customView = View.inflate(mContext, R.layout.listview_chat, null);

        TextView textViewName = (TextView) customView.findViewById(R.id.textViewName);
        TextView textViewText = (TextView) customView.findViewById(R.id.textViewText);
        TextView textViewTime = (TextView) customView.findViewById(R.id.textViewTime);

        Message message = listData.get(position);

        //fetch name and id of user that is chatting with this user
        if (listData.get(position).getFromName().equals(User.name)) {
            name = listData.get(position).getToName();
            userId =  listData.get(position).getToldId();
        }
        else {
            name = listData.get(position).getFromName();
            userId =  listData.get(position).getFromId();
        }
        textViewName.setText(name);
        textViewText.setText(message.getText());
        textViewTime.setText(getTimeFromUnixTime(Long.valueOf(message.getTimestamp())));


        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //transfer fetched data to chat activity
                if (listData.get(position).getFromName().equals(User.name)) {
                    name = listData.get(position).getToName();
                    userId =  listData.get(position).getToldId();
                }
                else {
                    name = listData.get(position).getFromName();
                    userId =  listData.get(position).getFromId();
                }
                Intent intent = new Intent(activity,ChatActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("userId",userId);
                activity.startActivity(intent);
            }
        });
        return customView;
    }

    private static String getTimeFromUnixTime(long milliseconds ) {
        Date time = new Date(milliseconds*1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }
}
