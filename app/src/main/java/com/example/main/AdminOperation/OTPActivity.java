package com.example.main.AdminOperation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main.R;
import com.example.main.Splash2Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
    EditText in1, in2, in3, in4, in5, in6;
    TextView textview;
    String getotpbackend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);

        in1 = findViewById(R.id.inputotp1);
        in2 = findViewById(R.id.inputotp2);
        in3 = findViewById(R.id.inputotp3);
        in4 = findViewById(R.id.inputotp4);
        in5 = findViewById(R.id.inputotp5);
        in6 = findViewById(R.id.inputotp6);

        final Button verifybuttonclick = findViewById(R.id.buttongetotp);

        textview = findViewById(R.id.textmobile);
        textview.setText(String.format("+91-%s", getIntent().getStringExtra("mobile")));

        getotpbackend = getIntent().getStringExtra("backendotp");
        final ProgressBar progressBarverifyotp = findViewById(R.id.progress_verifying_app);

        verifybuttonclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!in1.getText().toString().trim().isEmpty() && !in2.getText().toString().trim().isEmpty() && !in3.getText().toString().trim().isEmpty() && !in4.getText().toString().trim().isEmpty() && !in5.getText().toString().trim().isEmpty() && !in2.getText().toString().trim().isEmpty()) {
                    String entercodeotp = in1.getText().toString() +
                            in2.getText().toString() + in3.getText().toString() +
                            in4.getText().toString() + in5.getText().toString() +
                            in6.getText().toString();


                    if (getotpbackend != null) {
                        progressBarverifyotp.setVisibility(View.VISIBLE);
                        verifybuttonclick.setVisibility(View.INVISIBLE);

                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                                getotpbackend, entercodeotp
                        );
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBarverifyotp.setVisibility(View.GONE);
                                verifybuttonclick.setVisibility(View.VISIBLE);

                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), Splash2Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(OTPActivity.this, "Enter the correct otp", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(OTPActivity.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(OTPActivity.this,"otp verify",Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(OTPActivity.this, "Please enter all number", Toast.LENGTH_SHORT).show();
                }
            }
        });


        numberotpmove();

        TextView resendlabel = findViewById(R.id.textresendotp);
        resendlabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + getIntent().getStringExtra("mobile"),
                        60,
                        TimeUnit.SECONDS,
                        OTPActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newbackendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                                getotpbackend = newbackendotp;
                                Toast.makeText(OTPActivity.this, "OTP sended successfully", Toast.LENGTH_SHORT).show();

                            }
                        }

                );
            }
        });


    }

    private void numberotpmove() {
        in1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    in2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        in2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    in3.requestFocus();
                }
                else if(s.toString().trim().isEmpty())
                {
                    in1.requestFocus();
                }
                else {
                    in1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        in3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    in4.requestFocus();
                }
                else if(s.toString().trim().isEmpty())
                {
                    in2.requestFocus();
                }
                else {
                    in2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        in4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    in5.requestFocus();
                }
                else if(s.toString().trim().isEmpty())
                {
                    in3.requestFocus();
                }
                else {
                    in3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        in5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    in6.requestFocus();
                }
                else if(s.toString().trim().isEmpty())
                {
                    in4.requestFocus();
                }
                else{
                    in4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        in6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

               if(s.toString().trim().isEmpty()){
                   in5.requestFocus();
               }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(i);
        super.onBackPressed();
    }
}