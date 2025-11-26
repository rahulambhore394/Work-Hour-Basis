package com.example.main.VisitorOperation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class Info2Fragment extends Fragment {

    TextView detailname, detailemail, detailnumber, detailqual, detailsub, detailYear, detailBranch;
    TextView detailAcc, detailBank, detailBBranch, detailIFSC;

    FloatingActionButton deletebutton, updateButton;
    public  static  String name;
    String key = "";
    String vis_name;
    String detailURL = "";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info2, container, false);

        Context context = getContext();

        detailname = view.findViewById(R.id.detailname);
        detailemail = view.findViewById(R.id.detailemail);
        detailnumber = view.findViewById(R.id.detailnumber);
        detailqual = view.findViewById(R.id.detailqualification);
        detailsub = view.findViewById(R.id.detailsubject);
        deletebutton = view.findViewById(R.id.deleteButton);
        updateButton = view.findViewById(R.id.editButton);

        detailYear = view.findViewById(R.id.detailyear);
        detailBranch = view.findViewById(R.id.Visitor_branch_name);
        detailAcc = view.findViewById(R.id.detail_acc_no);
        detailBank = view.findViewById(R.id.detail_bank_name);
        detailBBranch = view.findViewById(R.id.detail_bank_branch);
        detailIFSC = view.findViewById(R.id.detail_ifsc);


        Intent intent = getActivity().getIntent();

        Bundle bundle = intent.getExtras();
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

            vis_name = detailname.getText().toString().trim();
            key = bundle.getString("Key");
            detailURL = bundle.getString("Name");

        }
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 name = detailname.getText().toString().trim();
                VisitorDataRetrieve.final_vis_name = name;

                DatabaseReference reference = FirebaseDatabase.getInstance(getString(R.string.database_instance)).getReference("Visitors");
                Query checkNameDatabase = reference.orderByChild("vis_name").equalTo(name);

                checkNameDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    final Intent intent = new Intent(getContext(), UpdateActivity.class);

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
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


                            intent.putExtra("vis_name", nameFromDB);
                            intent.putExtra("vis_email", emailFromDB);
                            intent.putExtra("vis_pnumber", numberFromDB);
                            intent.putExtra("vis_qualification", qualFromDB);
                            intent.putExtra("vis_subject", subFromDB);
                            intent.putExtra("vis_year", yearFromDB);
                            intent.putExtra("vis_Subject_branch", SbranchFromDB);
                            intent.putExtra("vis_acc_no", accFromDB);
                            intent.putExtra("vis_bank_name", bankFromDB);
                            intent.putExtra("vis_branch", BBranchFromDB);
                            intent.putExtra("vis_ifsc", ifscFromDB);

                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Not working", Toast.LENGTH_SHORT).show();
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
        return view;
        // Inflate the layout for this fragment
    }

    private void deleteRecord(String vis_name) {

        Context context = getContext();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance(getString(R.string.database_instance)).getReference("Visitors").child(vis_name);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure to delete the data ?");
        builder.setIcon(R.drawable.baseline_warning_24);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getContext(), Detail4Activity.class);

                Task<Void> mTask = databaseReference.removeValue();
                mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Data Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
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
}