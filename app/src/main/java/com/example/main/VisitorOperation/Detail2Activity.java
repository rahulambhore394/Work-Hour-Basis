package com.example.main.VisitorOperation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Detail2Activity extends AppCompatActivity {

    TextView detailname, detailemail, detailnumber, detailqual, detailsub,detailYear,detailBranch;
    TextView detailAcc,detailBank,detailBBranch,detailIFSC;

    FloatingActionButton deletebutton, updateButton;
    String key = "";
    String vis_name;
    String detailURL = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);

        detailname = findViewById(R.id.detailname);
        detailemail = findViewById(R.id.detailemail);
        detailnumber = findViewById(R.id.detailnumber);
        detailqual = findViewById(R.id.detailqualification);
        detailsub = findViewById(R.id.detailsubject);
        deletebutton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.editButton);

        detailYear = findViewById(R.id.detailyear);
        detailBranch = findViewById(R.id.Visitor_branch_name);
        detailAcc = findViewById(R.id.detail_acc_no);
        detailBank = findViewById(R.id.detail_bank_name);
        detailBBranch = findViewById(R.id.detail_bank_branch);
        detailIFSC = findViewById(R.id.detail_ifsc);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailname.setText(bundle.getString("Name"));
            detailemail.setText(bundle.getString("Email Address"));
            detailnumber.setText(bundle.getString("Phone number"));
            detailqual.setText(bundle.getString("Qualification"));
            detailsub.setText(bundle.getString("Subject"));
            detailYear.setText(bundle.getString("year"));
            detailBranch.setText(bundle.getString("branch_name"));


            detailAcc.setText(bundle.getString("Account_no"));
            detailBank.setText(bundle.getString("Bank_name"));
            detailBBranch.setText(bundle.getString("Bank_branch"));
            detailIFSC.setText(bundle.getString("IFSC"));


            //      intent.putExtra("year",dataList.get(holder.getAdapterPosition()).getVis_year());
      //      intent.putExtra("branch_name",dataList.get(holder.getAdapterPosition()).getVis_Subject_branch());
      //      intent.putExtra("Account_no",dataList.get(holder.getAdapterPosition()).getVis_acc_no());
    //        intent.putExtra("Bank_name",dataList.get(holder.getAdapterPosition()).getVis_bank_name());
  //          intent.putExtra("Bank_branch",dataList.get(holder.getAdapterPosition()).getVis_branch());
//            intent.putExtra("IFSC",dataList.get(holder.getAdapterPosition()).getVis_ifsc());



            vis_name = detailname.getText().toString().trim();
            key = bundle.getString("Key");
            detailURL = bundle.getString("Name");

        }


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = detailname.getText().toString().trim();

                DatabaseReference reference = FirebaseDatabase.getInstance(getString(R.string.database_instance)).getReference("Visitors");
                Query checkNameDatabase= reference.orderByChild("vis_name").equalTo(name);

                checkNameDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    final Intent intent = new Intent(getApplicationContext(),UpdateActivity.class);
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String nameFromDB = snapshot.child(name).child("vis_name").getValue(String.class);
                            String emailFromDB = snapshot.child(name).child("vis_email").getValue(String.class);
                            String numberFromDB = snapshot.child(name).child("vis_pnumber").getValue(String.class);
                            String qualFromDB = snapshot.child(name).child("vis_qualification").getValue(String.class);
                            String subFromDB = snapshot.child(name).child("vis_subject").getValue(String.class);
                            String yearFromDB = snapshot.child(name).child("vis_year").getValue(String.class);
                            String SbranchFromDB = snapshot.child(name).child("vis_Subject_branch").getValue(String.class);


                            String accFromDB = snapshot.child(name).child("vis_acc_no").getValue(String.class);
                            String bankFromDB = snapshot.child(name).child("vis_bank_name").getValue(String.class);
                            String BBranchFromDB = snapshot.child(name).child("vis_branch").getValue(String.class);
                            String ifscFromDB = snapshot.child(name).child("vis_ifsc").getValue(String.class);



                            intent.putExtra("vis_name",nameFromDB);
                            intent.putExtra("vis_email",emailFromDB);
                            intent.putExtra("vis_pnumber",numberFromDB);
                            intent.putExtra("vis_qualification",qualFromDB);
                            intent.putExtra("vis_subject",subFromDB);
                            intent.putExtra("vis_year",yearFromDB);
                            intent.putExtra("vis_Subject_branch",SbranchFromDB);
                            intent.putExtra("vis_acc_no",accFromDB);
                            intent.putExtra("vis_bank_name",bankFromDB);
                            intent.putExtra("vis_branch",BBranchFromDB);
                            intent.putExtra("vis_ifsc",ifscFromDB);


                            startActivity(intent);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Detail2Activity.this, "Not working", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });


        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord(vis_name);

            }
        });
    }


    public void deleteRecord(String vis_name) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(getString(R.string.database_instance)).getReference("Visitors").child(vis_name);
        AlertDialog.Builder builder = new AlertDialog.Builder(Detail2Activity.this);
        builder.setTitle("VisitorFunds");
        builder.setTitle("Are you sure to delete the data ?");
        builder.setIcon(R.drawable.baseline_warning_24);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);

                Task<Void> mTask = databaseReference.removeValue();
                mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Detail2Activity.this, "Data Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Detail2Activity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }


    @Override
    public void onBackPressed() {

        Intent i = new Intent(getApplicationContext(),DetailActivity.class);
        startActivity(i);

        super.onBackPressed();
    }
}