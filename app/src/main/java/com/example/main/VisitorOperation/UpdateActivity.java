package com.example.main.VisitorOperation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UpdateActivity extends AppCompatActivity {


    EditText updateVName, updateVEmail, updateVPnumber, updateVQualification, updateVSubject;
    EditText updateAcc_no,updateBBranch,updateBank,updateIFSC;
    Spinner spinner1,spinner2;
    String  []year = new String[]{"Select year","1st year","2nd year","3rd year","4th Year"};
    String  []branch = new String[]{"Select branch",
            "Instrumentation Engineering",
            " Civil Engineering",
            " Computer Engineering",
            "Chemical Engineering",
            "Production Engineering",
            "Electrical Engineering",
            "Electronics and Telecommunication Engineering",
            "Information Technology",
            "Mechanical Engineering"};

    ArrayAdapter<String> adapter1,adapter2;
    Button updateButton;
    String nameVisitor, emailVisitor, numberVisitor, qualVisitor, subVisitor,yearVisitor,branchVisitor;
    String Acc_no_Visitor,bankVisitor,bankBranchVisitor,IFSCVisitor;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        updateVName = findViewById(R.id.updateVname);
        updateVEmail = findViewById(R.id.updateVemail);
        updateVPnumber = findViewById(R.id.updateVPnumber);
        updateVQualification = findViewById(R.id.updateVQualification);
        updateVSubject = findViewById(R.id.updateVSubject);

        spinner1 = findViewById(R.id.year);
        spinner2 = findViewById(R.id.branch_name);

        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, year);
        spinner1.setAdapter(adapter1);
        spinner1.setSelection(0);

        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, branch);
        spinner2.setAdapter(adapter2);
        spinner2.setSelection(0);


        updateAcc_no = findViewById(R.id.updateVis_acc_no);
        updateBank = findViewById(R.id.updateVis_bank_name);
        updateBBranch = findViewById(R.id.updateVis_branch_name);
        updateIFSC = findViewById(R.id.updateVis_ifsc);

        updateButton = findViewById(R.id.updateButton);



        databaseReference = FirebaseDatabase.getInstance(getString(R.string.database_instance)).getReference("Visitors");

        showData();
        updateButton.setOnClickListener(v -> {
            if(isNameChanged()||isEmailChanged()||isNumberChanged()||isQualChanged()||isSubjectChanged()||isAccNoChanged()||isBankChanged()||isBranchBankChanged()||isIFSCChanged())
            {
                Toast.makeText(UpdateActivity.this, "Data updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),DetailActivity.class));
            }
            else
            {
                Toast.makeText(UpdateActivity.this, "No changes found", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public boolean isNameChanged() {
        if (!nameVisitor.equals(updateVName.getText().toString())) {
            databaseReference.child(nameVisitor).child("vis_name").setValue(updateVName.getText().toString());
            nameVisitor = updateVName.getText().toString();

            return true;
        } else {
            return false;
        }
    }

    public boolean isEmailChanged() {
        if (!emailVisitor.equals(updateVEmail.getText().toString())) {
            databaseReference.child(nameVisitor).child("vis_email").setValue(updateVEmail.getText().toString());
            emailVisitor = updateVEmail.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    public boolean isNumberChanged() {
        if (!numberVisitor.equals(updateVPnumber.getText().toString())) {
            databaseReference.child(nameVisitor).child("vis_pnumber").setValue(updateVPnumber.getText().toString());
            numberVisitor = updateVPnumber.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    public boolean isQualChanged() {
        if (!qualVisitor.equals(updateVQualification.getText().toString())) {
            databaseReference.child(nameVisitor).child("vis_qualification").setValue(updateVQualification.getText().toString());
            qualVisitor = updateVQualification.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    public boolean isSubjectChanged() {
        if (!subVisitor.equals(updateVSubject.getText().toString())) {
            databaseReference.child(nameVisitor).child("vis_subject").setValue(updateVSubject.getText().toString());
            subVisitor = updateVSubject.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    public boolean isAccNoChanged() {
        if (!Acc_no_Visitor.equals(updateAcc_no.getText().toString())) {
            databaseReference.child(nameVisitor).child("vis_acc_no").setValue(updateAcc_no.getText().toString());
            Acc_no_Visitor = updateAcc_no.getText().toString();

            return true;
        } else {
            return false;
        }
    }
    public boolean isBankChanged() {
        if (!bankVisitor.equals(updateBank.getText().toString())) {
            databaseReference.child(nameVisitor).child("vis_bank_name").setValue(updateBank.getText().toString());
            bankVisitor = updateBank.getText().toString();

            return true;
        } else {
            return false;
        }
    }
    public boolean isBranchBankChanged() {
        if (!bankBranchVisitor.equals(updateBBranch.getText().toString())) {
            databaseReference.child(nameVisitor).child("vis_branch").setValue(updateBBranch.getText().toString());
            bankBranchVisitor = updateBBranch.getText().toString();

            return true;
        } else {
            return false;
        }
    }
    public boolean isIFSCChanged() {
        if (!IFSCVisitor.equals(updateIFSC.getText().toString())) {
            databaseReference.child(nameVisitor).child("vis_ifsc").setValue(updateIFSC.getText().toString());
            IFSCVisitor = updateIFSC.getText().toString();

            return true;
        } else {
            return false;
        }
    }

    public boolean isYearChanged() {
        if (!yearVisitor.equals(spinner1.getSelectedItem().toString())) {
            databaseReference.child(nameVisitor).child("vis_year").setValue(spinner1.getSelectedItem().toString());
            yearVisitor = spinner1.getSelectedItem().toString();

            return true;
        } else {
            return false;
        }
    }

    public boolean isSBranchChanged() {
        if (!bankBranchVisitor.equals(spinner2.getSelectedItem().toString())) {
            databaseReference.child(nameVisitor).child("vis_Subject_branch").setValue(spinner2.getSelectedItem().toString());
            branchVisitor= spinner2.getSelectedItem().toString();

            return true;
        } else {
            return false;
        }
    }


    public void showData() {
        Intent intent = getIntent();
        nameVisitor = intent.getStringExtra("vis_name");
        emailVisitor = intent.getStringExtra("vis_email");
        numberVisitor = intent.getStringExtra("vis_pnumber");
        qualVisitor = intent.getStringExtra("vis_qualification");
        subVisitor = intent.getStringExtra("vis_subject");
        yearVisitor = intent.getStringExtra("vis_year");
        branchVisitor = intent.getStringExtra("vis_Subject_branch");
        Acc_no_Visitor=intent.getStringExtra("vis_acc_no");
        bankVisitor=intent.getStringExtra("vis_bank_name");
        bankBranchVisitor=intent.getStringExtra("vis_branch");
        IFSCVisitor=intent.getStringExtra("vis_ifsc");

        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);
        updateVName.setText(nameVisitor);
        updateVEmail.setText(emailVisitor);
        updateVPnumber.setText(numberVisitor);
        updateVQualification.setText(qualVisitor);
        updateVSubject.setText(subVisitor);


        updateAcc_no.setText(Acc_no_Visitor);
        updateIFSC.setText(IFSCVisitor);
        updateBank.setText(bankVisitor);
        updateBBranch.setText(bankBranchVisitor);


    }
    @Override
    public void onBackPressed() {

        Intent i = new Intent(getApplicationContext(),Detail4Activity.class);
        startActivity(i);

        super.onBackPressed();
    }

}