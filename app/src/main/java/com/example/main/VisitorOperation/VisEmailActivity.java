package com.example.main.VisitorOperation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.main.AdminOperation.CommanActivity;
import com.example.main.R;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import java.net.URL;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class VisEmailActivity extends AppCompatActivity {

    EditText Confirmemail;
    Button ConfrimBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vis_email);

        Confirmemail = findViewById(R.id.VisConfirmEmail);
        ConfrimBtn = findViewById(R.id.VisConfirmButton);
        Confirmemail.setText(EmailConfirmation.VisEmail);

        ConfrimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSendEmail();

                Intent i = new Intent(getApplicationContext(), CommanActivity.class);
                Toast.makeText(VisEmailActivity.this, "Email send successfully !!!", Toast.LENGTH_SHORT).show();
                startActivity(i);
            }
        });

    }


    public void buttonSendEmail() {
        try {

//            String stringSender = "harrygamers804@gmail.com";
//            String stringReceiver = EmailConfirmation.VisEmail;
//            String stringPasswordSenderEmail = "cofctzbdjelhenmb";

            String stringSender = "patilbhavesh8140@gmail.com";
            String stringReceiver = EmailConfirmation.VisEmail;
            String stringPasswordSenderEmail = "dwfiffvyhjcbdzay";

            String stringHost = "smtp.gmail.com";

            Properties properties = new Properties();

            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");


            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {

                    return new PasswordAuthentication(stringSender,stringPasswordSenderEmail);
                }
            });



            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(stringReceiver));
            mimeMessage.setSubject("Subject : Visitor application");
            mimeMessage.setText("USername : "+Confirmemail.getText().toString() + "\n\n You have added Successfully!!!\nTo install the app click on the link below...\n https://clientapplication.page.link/applink");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    }
                    catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();


        } catch (MessagingException e){
            e.printStackTrace();
        }

    }
}