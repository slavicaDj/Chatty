package com.example.hpkorisnik.chatty.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hpkorisnik.chatty.R;
import com.example.hpkorisnik.chatty.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference root;

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ImageView imageView;

    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        imageView = (ImageView) findViewById(R.id.imageView);
        Picasso.with(this.getApplicationContext()).load(R.drawable.chatty2).into(imageView);

        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                //// TODO: 18-Apr-17 Add loading screen 
                
                final String email = editTextEmail.getText().toString();
                final String password = editTextPassword.getText().toString();
                final String name = editTextName.getText().toString();

                Task<AuthResult> task = auth.createUserWithEmailAndPassword(email,password);
                task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //switch to lower level of nodes
                            root = FirebaseDatabase.getInstance().getReference().getRoot().child("users");
                            String id = root.push().getKey();

                            //initialize user; he will be accessible from every class
                            User.name = name;
                            User.email = email;
                            User.id = id;
                            root.child(id).setValue("");
                            
                            //now root needs to become users branch, so I can append values
                            root = root.child(id);
                            Map<String,Object> map = new HashMap<>();
                            map.put("email",email);
                            map.put("name",name);
                            root.updateChildren(map);
                            
                            //notify user about successful registration
                            Toast.makeText(RegisterActivity.this, "You registered successfully.", Toast.LENGTH_SHORT).show();

                            //transfer user to main activity in order to chat with other users
                            Intent intent = new Intent(RegisterActivity.this, ConversationsActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Your registration failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

}
