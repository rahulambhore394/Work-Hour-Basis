package com.example.main.VisitorOperation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main.AdminOperation.CommanActivity;
import com.example.main.AdminOperation.LoginActivity;
import com.example.main.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class VisitorActivity extends AppCompatActivity {


    Button saveButton;
    EditText vis_name, vis_email, vis_pnumber, vis_qualification, vis_subject, vis_acc_no, vis_bank_name, vis_branch, vis_ifsc;


    Spinner spinner1, spinner2;
    String[] year = new String[]{"Select year", "1st year", "2nd year", "3rd year","4th year"};
    String[] branch = new String[]{"Select branch", "Production Engineering",
            "Instrumentation Engineering",
            " Civil Engineering",
            " Computer Science and Engineering",
            "Chemical Engineering",
            "Textile Engineering",
            "Electrical Engineering",
            "Electronics and Telecommunication Engineering",
            "Information Technology",
            "Mechanical Engineering"};

    ArrayAdapter<String> adapter1, adapter2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //      requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_visitor);


        //here,we just fetch ID of each content and pass to objects

        vis_name = findViewById(R.id.uploadVname);
        vis_email = findViewById(R.id.uploadVemail);
        vis_pnumber = findViewById(R.id.uploadVPnumber);
        vis_qualification = findViewById(R.id.uploadVQualification);
        vis_subject = findViewById(R.id.uploadVSubject);


        spinner1 = findViewById(R.id.year);
        spinner2 = findViewById(R.id.branch_name);

        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, year);
        spinner1.setAdapter(adapter1);
        spinner1.setSelection(0);

        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, branch);
        spinner2.setAdapter(adapter2);
        spinner2.setSelection(0);

        vis_acc_no = findViewById(R.id.uploadVis_acc_no);
        vis_bank_name = findViewById(R.id.uploadVis_bank_name);
        vis_branch = findViewById(R.id.uploadVis_branch_name);
        vis_ifsc = findViewById(R.id.uploadVis_ifsc);

        saveButton = findViewById(R.id.saveButton);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name = vis_name.getText().toString();
                String email = vis_email.getText().toString();

                EmailConfirmation.VisEmail = email;

                String number = vis_pnumber.getText().toString();
                String qualification = vis_qualification.getText().toString();
                String subject = vis_subject.getText().toString();
                String acc_no = vis_acc_no.getText().toString();
                String bank_name = vis_bank_name.getText().toString();
                String branch_name = vis_branch.getText().toString();
                String ifsc = vis_ifsc.getText().toString();
                String year = spinner1.getSelectedItem().toString();
                String branch_sub = spinner2.getSelectedItem().toString();


                if (name.isEmpty() && number.isEmpty() && subject.isEmpty()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(VisitorActivity.this);
                    builder.setTitle("VisitorFunds");
                    builder.setMessage("Please fill the form");
                    builder.setIcon(R.drawable.baseline_warning_24);

                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });
                    builder.create().show();
                }
                else if (email.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(VisitorActivity.this);
                    builder.setTitle("VisitorFunds");
                    builder.setMessage("Visitor's Email cannot be empty");
                    builder.setIcon(R.drawable.baseline_warning_24);

                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });
                    builder.create().show();
                } else {
                    String username = vis_name.getText().toString().trim();
                    VisitorDataRetrieve dataRetrieve = new VisitorDataRetrieve(name, email, number, qualification, subject, year, branch_sub, acc_no, bank_name, branch_name, ifsc);

                    //loading
                    AlertDialog.Builder builder = new AlertDialog.Builder(VisitorActivity.this);
                    builder.setCancelable(false);
                    builder.setView(R.layout.progress_layout);

                    builder.create().show();

                    //   String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    FirebaseDatabase.getInstance(getString(R.string.database_instance)).getReference("Visitors").child(username).setValue(dataRetrieve)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(VisitorActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), VisEmailActivity.class);
                                        startActivity(i);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(VisitorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }

        });
    }

}