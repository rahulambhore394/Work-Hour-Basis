package com.example.main.AdminOperation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main.FirebaseUtils;
import com.example.main.R;
import com.example.main.VisitorOperation.VisEmailActivity;
import com.example.main.VisitorOperation.VisitorActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SalaryActivity extends AppCompatActivity {

    Button salary_btn;
    EditText th, pr;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary);

        th = findViewById(R.id.vis_theory);
        pr = findViewById(R.id.vis_practical);
        salary_btn = findViewById(R.id.visitor_th_pr);


        salary_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String theory = th.getText().toString();
                String practical = pr.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(SalaryActivity.this);
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                builder.setTitle("VisitorFunds");
                builder.setMessage("Visitor's salary has fixed successfully !");
                builder.setIcon(R.drawable.baseline_thumb_up_24);
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //    Toast.makeText(SalaryActivity.this, "saved", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), CommanActivity.class);
                        startActivity(i);
                        dialog.cancel();
                    }
                });
                builder.create().show();

                AdminHelperClass adminHelperClass = new AdminHelperClass(theory, practical);
                adminHelperClass.theory = theory;
                adminHelperClass.practical = practical;
                FirebaseUtils.getDatabase().getReference("Salary").setValue(adminHelperClass)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Toast.makeText(SalaryActivity.this, "saved", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });

    }
}