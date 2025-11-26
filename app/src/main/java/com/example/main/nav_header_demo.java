package com.example.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.example.main.AdminOperation.Admin_Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class nav_header_demo extends AppCompatActivity {


    TextView admin_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_header_demo);


 //       SharedPreferences preferences = getSharedPreferences("Myuser", Context.MODE_PRIVATE);
   //     SharedPreferences.Editor editor = preferences.edit();
     //   String getName = preferences.getString("name","");


//        admin_name = findViewById(R.id.admin_name);
        String username = Admin_Constant.admin_username;

        System.out.println("Username:" + username);
        DatabaseReference zonesRef = FirebaseDatabase.getInstance(getString(R.string.database_instance)).getReference("users").child("name");
        zonesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("Count : "+dataSnapshot.getChildrenCount());
                for (DataSnapshot snapshot : dataSnapshot.getChildren())

                {
                    String ad_name = snapshot.child("name").getValue(String.class);
                    String ad_username = snapshot.child("username").getValue(String.class);
                    System.out.println("name : "+ad_name);

                    if(Objects.equals(ad_username,username))
                    {

                        admin_name.setText(username);
                    }
                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}