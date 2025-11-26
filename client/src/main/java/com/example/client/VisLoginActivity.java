package com.example.client;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
//import com.example.main.VisitorOperation.*;
import java.util.Objects;

public class VisLoginActivity extends AppCompatActivity {

    EditText loginEmail;
    Button login1;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vis_login);

        loginEmail = findViewById(R.id.login_Vemail);
        login1 = findViewById(R.id.login_button);

        login1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail()) {

                } else {
                    checkUser();

                }
            }
        });
    }

    public Boolean validateEmail() {
        String val = loginEmail.getText().toString();
        if (val.isEmpty()) {
            loginEmail.setError("Email Address cannot be empty");
            return false;
        } else {
            loginEmail.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userLoginEmail = loginEmail.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance("https://retrieve-show-default-rtdb.firebaseio.com/").getReference("Visitors");
        Query checkUserDatabase = reference.orderByChild("vis_email").equalTo(userLoginEmail);


        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String userLEmail = loginEmail.getText().toString().trim().toLowerCase();
                    loginEmail.setError(null);
                    String userFromDB = snapshot.child("vis_email").getValue(String.class);
                    if (!Objects.equals(userFromDB, userLEmail))
                    // if(Objects.equals(userFromDB, userLoginEmail))
                    {
                        loginEmail.setError(null);
                        Intent i = new Intent(getApplicationContext(),VisInfoActivity.class);


                        String nameFromDB = snapshot.child("vis_email").getValue(String.class);
                        String emailFromDB = snapshot.child("vis_name").getValue(String.class);
                        String numberFromDB = snapshot.child("vis_pnumber").getValue(String.class);
                        String qualFromDB = snapshot.child("vis_qualification").getValue(String.class);
                        String subjectFromDB = snapshot.child("vis_subject").getValue(String.class);




                        startActivity(i);

                    } else {
                        loginEmail.setError("Invalid Credentials");
                        loginEmail.requestFocus();
                    }
                } else {
                    loginEmail.setError("Visitor's Email does not exist");
                    loginEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

}