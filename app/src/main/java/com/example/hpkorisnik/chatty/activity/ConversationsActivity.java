package com.example.hpkorisnik.chatty.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hpkorisnik.chatty.adapter.CustomListAdapterConversations;
import com.example.hpkorisnik.chatty.object.Message;
import com.example.hpkorisnik.chatty.R;
import com.example.hpkorisnik.chatty.object.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class ConversationsActivity extends AppCompatActivity {

    private TextView textViewName;
    private ListView listView;
    private Button buttonLogout;
    private Button buttonNewMessage;
    private DatabaseReference root;
    private ArrayList<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messages = new ArrayList<>();

        textViewName = (TextView) findViewById(R.id.textViewName);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonNewMessage = (Button) findViewById(R.id.buttonNewMessage);

        listView = (ListView) findViewById(R.id.listView);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ConversationsActivity.this, ChatActivity.class);

                //fetch name and id of user that is chatting with this user
                String name = null;
                String userId = null;
                if (messages.get(position).getFromName().equals(User.name)) {
                    name = messages.get(position).getToName();
                    userId =  messages.get(position).getToldId();
                }
                else {
                    name = messages.get(position).getFromName();
                    userId =  messages.get(position).getFromId();
                }

                //transfer fetched data to chat activity
                intent.putExtra("name",name);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });*/

        root = FirebaseDatabase.getInstance().getReference().getRoot().child("user-messages");
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("first listener: ","user-messages");

                //empty previous messages
                messages.clear();

                //get user-messages:our_user_id node
                DataSnapshot child = dataSnapshot.child(User.id);

                //loop through all children of user-messages:our_user_id node to get all people main user chatted with
                for (DataSnapshot grandChildNode : child.getChildren()) {
                    final Message message = new Message();
                    messages.add(message);

                    //check only last message between myId and found userId using Query (source: stackoverflow)
                    Query lastQuery = grandChildNode.getRef().orderByKey().limitToLast(1);
                    lastQuery.keepSynced(true);
                    lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i("second listener: ","query");
                            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                            if (iterator.hasNext()) {
                                message.setId(iterator.next().getKey());
                                Log.i("messageId: ",message.getId());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                DatabaseReference rootMessages = FirebaseDatabase.getInstance().getReference().getRoot().child("messages");
                rootMessages.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("third listener: ","messages");

                        //loop through all messages to fetch missing information
                        //at this stage, we only have messageId, fetched from user-messages part of structure
                        Log.i("messages size: ","" + messages.size());
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
                                    Log.i("forth listener: ","users");
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
                                        Log.i("message:",message.toString());
                                    }
                                    //ArrayAdapter<Message> adapter = new ArrayAdapter<>(ConversationsActivity.this, android.R.layout.simple_list_item_1, messages);
                                    CustomListAdapterConversations adapter = new CustomListAdapterConversations(ConversationsActivity.this,ConversationsActivity.this.getApplicationContext(),messages);
                                    listView.setAdapter(adapter);
                                    textViewName.setText(User.name);
                                }

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

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 20-Apr-17 make dialog
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent intent = new Intent(ConversationsActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConversationsActivity.this,ContactsActivity.class);
                startActivity(intent);
            }
        });
    }

}
