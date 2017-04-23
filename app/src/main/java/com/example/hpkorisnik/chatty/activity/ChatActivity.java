package com.example.hpkorisnik.chatty.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hpkorisnik.chatty.adapter.MessageAdapter;
import com.example.hpkorisnik.chatty.object.Message;
import com.example.hpkorisnik.chatty.R;
import com.example.hpkorisnik.chatty.object.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ListView listView;
    private Button buttonSendMessage;
    private EditText editTextMessage;
    TextView textViewName;

    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();

        listView = (ListView) findViewById(R.id.listView);

        editTextMessage = (EditText) findViewById(R.id.editTextMessage);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewName.setText(intent.getExtras().getString("name"));

        final String chatWithId = intent.getExtras().getString("userId");

        buttonSendMessage = (Button) findViewById(R.id.buttonSendMessage);
        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("KLIK","----------");
                //get information related to message
                String text = editTextMessage.getText().toString();
                String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
                String fromId = User.id;
                String toId = chatWithId;

                //push message to messages structure in database
                DatabaseReference rootMessages = FirebaseDatabase.getInstance().getReference().getRoot().child("messages");
                String id = rootMessages.push().getKey();
                rootMessages.child(id).setValue("");
                rootMessages = rootMessages.child(id);
                Map<String,Object> mapMessage = new HashMap<>();
                mapMessage.put("fromId",fromId);
                mapMessage.put("text",text);
                mapMessage.put("timestamp",timestamp);
                mapMessage.put("toId",toId);

                //push message key to user-message structure for both users
                DatabaseReference rootUserMessages = FirebaseDatabase.getInstance().getReference().getRoot().child("user-messages");
                //todo make new node if it doesnt exist
                DatabaseReference rootUserMessagesFirstUser = rootUserMessages.child(User.id).child(chatWithId);
                DatabaseReference rootUserMessagesSecondUser = rootUserMessages.child(chatWithId).child(User.id);
                Map<String,Object> mapUserMessage = new HashMap<>();
                mapUserMessage.put(id,1);
                rootUserMessagesFirstUser.updateChildren(mapUserMessage);
                rootUserMessagesSecondUser.updateChildren(mapUserMessage);
                rootMessages.updateChildren(mapMessage);

                editTextMessage.setText("");
            }
        });


        root = FirebaseDatabase.getInstance().getReference().getRoot().child("user-messages").child(User.id).child(chatWithId);
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clear previous data from listView; necessary because of arrayadapter data duplication
                final ArrayList<Message> messages = new ArrayList<>();

                //looping through all messages in conversation chosen in previous activity from ListView and collecting their keys
                for (DataSnapshot childNode : dataSnapshot.getChildren()) {
                    Message message = new Message();
                    message.setId(childNode.getKey());
                    messages.add(message);
                }

                DatabaseReference rootMessages = FirebaseDatabase.getInstance().getReference().getRoot().child("messages");
                rootMessages.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //loop through all messages to fetch missing information
                        //at this stage, we only have messageId, fetched from user-messages part of structure
                        for (Message message : messages) {
                            DataSnapshot childNode = dataSnapshot.child(message.getId());
                            message.setFromId(childNode.child("fromId").getValue().toString());
                            message.setToldId(childNode.child("toId").getValue().toString());
                            message.setTimestamp(childNode.child("timestamp").getValue().toString());
                            message.setText(childNode.child("text").getValue().toString());
                            Log.i("textOfMessage",message.getText());

                            //loop through all messages of main user to find names of other users that sent him message
                            //at this stage we have only ids
                            DatabaseReference rootUsers = FirebaseDatabase.getInstance().getReference().getRoot().child("users");
                            rootUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (Message message : messages) {
                                        if (User.id.equals(message.getFromId())) {
                                            //fetch toId
                                            message.setFromName(User.name);
                                            message.setToName(dataSnapshot.child(message.getToldId()).child("name").getValue().toString());
                                        }
                                        if (User.id.equals(message.getToldId())) {
                                            //fetch fromId
                                            message.setFromName(dataSnapshot.child(message.getFromId()).child("name").getValue().toString());
                                            message.setToName(User.name);
                                        }
                                    }
                                    MessageAdapter adapter = new MessageAdapter(ChatActivity.this, R.layout.item_chat_left, messages);
                                    listView.setAdapter(adapter);                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
