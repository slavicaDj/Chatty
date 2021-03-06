package com.example.hpkorisnik.chatty.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hpkorisnik.chatty.R;
import com.example.hpkorisnik.chatty.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class LoginActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private DatabaseReference root;
    private ImageView imageView;
    public static Task<AuthResult> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog =  new ProgressDialog(LoginActivity.this);

        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);

        imageView = (ImageView) findViewById(R.id.imageView);
        Picasso.with(this.getApplicationContext()).load(R.drawable.logo).into(imageView);

        buttonLogin = (Button)findViewById(R.id.buttonLogin);
        buttonRegister = (Button)findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*                //set progress dialog
                final ProgressDialog dialog = new ProgressDialog(LoginActivity.this.getApplicationContext());
                dialog.setMessage("loading");
                dialog.show();*/

                final String email = editTextEmail.getText().toString();
                final String password = editTextPassword.getText().toString();

                //check credentials
                final FirebaseAuth auth = FirebaseAuth.getInstance();
                task = auth.signInWithEmailAndPassword(email, password);
                task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //set email for main user
                        User.email = email;

                        //iterate through all users to get name of user with entered email
                        DatabaseReference rootLogin = FirebaseDatabase.getInstance().getReference().getRoot().child("users");
                        rootLogin.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot childNode : dataSnapshot.getChildren()) {
                                    if (childNode.child("email").getValue().equals(email)) {
                                        User.name = (String) childNode.child("name").getValue();
                                        break;
                                    }
                                }
                                User.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                if (User.id != null) {
                                    //dispose dialog show ok
                                    Toast.makeText(LoginActivity.this, "Your are logged in.", Toast.LENGTH_SHORT).show();
                                    Log.i("userId", User.id);
                                    Intent intent = new Intent(LoginActivity.this, ConversationsActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    //dispose dialog show not ok
                                    Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                            });
                    }
                });
            }//onClick
        });//setOnClickListener

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

    }//onCreate

}//LoginActivity
