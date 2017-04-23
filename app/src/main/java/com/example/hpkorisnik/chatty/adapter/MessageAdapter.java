package com.example.hpkorisnik.chatty.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hpkorisnik.chatty.R;
import com.example.hpkorisnik.chatty.object.Message;
import com.example.hpkorisnik.chatty.object.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private Activity activity;
    private List<Message> messages;

    public MessageAdapter(Activity context, int resource, List<Message> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.messages = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        int layoutResource = 0; // determined by view type
        Message chatMessage = getItem(position);
        int viewType = getItemViewType(position);

        if (chatMessage.getToldId().equals(User.id)) {
            layoutResource = R.layout.item_chat_left;
        } else {
            layoutResource = R.layout.item_chat_right;
        }

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        //set message content
        holder.msg.setText(chatMessage.getText());
        holder.time.setText(getTimeFromUnixTime(Long.valueOf(chatMessage.getTimestamp())));


        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }

    private class ViewHolder {
        private TextView msg;
        private TextView time;

        public ViewHolder(View v) {
            msg = (TextView) v.findViewById(R.id.txt_msg);
            time = (TextView) v.findViewById(R.id.textViewTime);
        }
    }

    private static String getTimeFromUnixTime(long milliseconds ) {
        Date time = new Date(milliseconds*1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }
}