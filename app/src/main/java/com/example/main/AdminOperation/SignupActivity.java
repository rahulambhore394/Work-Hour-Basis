package com.example.main.AdminOperation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;


public class SignupActivity extends AppCompatActivity {


    EditText signupName,signupEmail,signupUsername,signupPassword,mobile;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
  //      getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        mobile = findViewById(R.id.signup_mobile);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance("https://clock-hour-basis-default-rtdb.firebaseio.com/");
                reference = database.getReference("users");
                String name = signupName.getText().toString();
                String email = signupEmail.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();
                String number = mobile.getText().toString();
                ProgressBar progressBar = findViewById(R.id.progress_sending_app);

                AdminHelperClass adminHelperClass =  new AdminHelperClass(name,email,username,password,number);
                reference.child(name).setValue(adminHelperClass);


                if(!mobile.getText().toString().trim().isEmpty()){

                    if((mobile.getText().toString().trim()).length()==10){

                        progressBar.setVisibility(View.VISIBLE);
                        signupButton.setVisibility(View.INVISIBLE);

                        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + mobile.getText().toString(), 60, TimeUnit.SECONDS, SignupActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.GONE);
                                signupButton.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                signupButton.setVisibility(View.VISIBLE);
                                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String backendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                progressBar.setVisibility(View.GONE);
                                signupButton.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(getApplicationContext(),OTPActivity.class);
                                intent.putExtra("mobile",mobile.getText().toString());
                                intent.putExtra("backendotp",backendotp);
                                startActivity(intent);

                            }
                        });

                    }
                    else {
                        Toast.makeText(SignupActivity.this,"Please enter correct number",Toast.LENGTH_SHORT).show();

                    }
                }
                else{
                    Toast.makeText(SignupActivity.this,"Please fill the form",Toast.LENGTH_SHORT).show();
                }

                /*AdminHelperClass adminHelperClass = new AdminHelperClass(name, email, username, password);
                reference.child(name).setValue(adminHelperClass);

                Toast.makeText(SignupActivity.this, "You have signup successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);*/

            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        super.onBackPressed();
    }
}