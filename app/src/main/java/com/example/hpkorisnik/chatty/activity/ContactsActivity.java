package com.example.hpkorisnik.chatty.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.hpkorisnik.chatty.adapter.CustomListAdapterContacts;
import com.example.hpkorisnik.chatty.R;
import com.example.hpkorisnik.chatty.object.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseReference root;
    private ArrayList<String> contacts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        listView = (ListView) findViewById(R.id.listView);

        root = FirebaseDatabase.getInstance().getReference().getRoot().child("users");
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childNode : dataSnapshot.getChildren()) {
                    if (!childNode.getKey().equals(User.id)) {
                        contacts.add(childNode.getKey() + "#" + childNode.child("name").getValue());
                    }
                }
                for (String s : contacts) {
                    Log.i("contact",s);
                }
                CustomListAdapterContacts adapter = new CustomListAdapterContacts(ContactsActivity.this,ContactsActivity.this.getApplicationContext(),contacts);
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
