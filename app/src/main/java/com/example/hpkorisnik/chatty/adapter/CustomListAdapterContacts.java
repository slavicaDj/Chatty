package com.example.hpkorisnik.chatty.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hpkorisnik.chatty.R;
import com.example.hpkorisnik.chatty.activity.ChatActivity;

import java.util.ArrayList;

public class CustomListAdapterContacts extends BaseAdapter {

    private ArrayList<String> listData;
    private Context mContext;
    private Activity activity;

    private String name;
    private String userId;

    public CustomListAdapterContacts(Activity activity, Context aContext, ArrayList<String> listData) {

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

        View customView = View.inflate(mContext, R.layout.listview_contacts, null);

        final String contact = listData.get(position);

        TextView textView = (TextView) customView.findViewById(R.id.textView);

        textView.setText(contact.split("#")[1]);


        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                userId = contact.split("#")[0];
                name = contact.split("#")[1];
                intent.putExtra("name", name);
                intent.putExtra("userId",userId);
                mContext.startActivity(intent);
                activity.finish();
            }
        });
        return customView;
    }
}