package com.example.main.AdminOperation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.main.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class About_Admin_Activity extends AppCompatActivity {

    TextView profileName, profileEmail, profileUsername, profilePassword, profileNumber;
    String public_username;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_admin);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        profilePassword = findViewById(R.id.profilePassword);
        profileNumber = findViewById(R.id.profileNumber);

        AlertDialog.Builder builder = new AlertDialog.Builder(About_Admin_Activity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        public_username = Admin_Constant.admin_username;


        DatabaseReference zonesRef = FirebaseDatabase.getInstance(getString(R.string.database_instance)).getReference("users");
        dialog.show();
        zonesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("Count: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String admin_name = snapshot.child("name").getValue().toString();
                    String admin_username = snapshot.child("username").getValue().toString();
                    String admin_password = snapshot.child("password").getValue().toString();
                    String admin_number = snapshot.child("number").getValue().toString();
                    String admin_email = snapshot.child("email").getValue().toString();

                    System.out.println("admin_username" + admin_username + "\n");
                    System.out.println(public_username);

                    if (Objects.equals(admin_username, public_username)) {
                        System.out.println(" Finally: " + admin_email);

                        profileName.setText(admin_name);
                        profileNumber.setText(admin_number);
                        profileEmail.setText(admin_email);
                        profileUsername.setText(admin_username);
                        profilePassword.setText(admin_password);


                        dialog.dismiss();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(About_Admin_Activity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });

    }
}