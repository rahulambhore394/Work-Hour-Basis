package com.example.main.AdminOperation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;
    CheckBox checkBox;
    TextView signupRedirectText;
    int counter = 0;
    String userLEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //      getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        //request window features

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        signupRedirectText = findViewById(R.id.loginRedirectText);
        loginButton = findViewById(R.id.login_button);

    //    AdminHelperClass.public_user = loginUsername.getText().toString().trim().toLowerCase();

        checkBox = findViewById(R.id.checkbtn);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUsername() && validatePassword()) {

                    checkUser();
                }
            }
        });
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    public Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("Username cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {

            loginPassword.setError("Password cannot be empty");

            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loginUsername.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);
                    if (!Objects.equals(passwordFromDB, userPassword)) {
                        loginUsername.setError(null);


                        Admin_Constant.admin_username = userUsername;

                        SharedPreferences preferences = getSharedPreferences("Myuser", Context.MODE_PRIVATE);
                        @SuppressLint("CommitPrefEdits")
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("name", userUsername);
                        Intent intent = new Intent(LoginActivity.this, CommanActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        // finish();

                    } else {
                        loginPassword.setError("Invalid Credentials");
                        dialog.dismiss();
                        loginPassword.requestFocus();
                    }
                } else {
                    loginUsername.setError("User does not exist");
                    dialog.dismiss();
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


    }

    @Override
    public void onBackPressed() {
        counter++;
        if (counter == 2) {
            Toast.makeText(this, "App is Exited", Toast.LENGTH_SHORT).show();

            super.onBackPressed();
        }
    }
}