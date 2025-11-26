package com.example.main.UploadPDF_Operation;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class UploadPDFActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    List<pdfClass> uploads;
    Button upload_btn;
    EditText pdf_name;
    PdfAdapter pdfAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        uploads = new ArrayList<>();
        pdfAdapter = new PdfAdapter(this, uploads);
        recyclerView.setAdapter(pdfAdapter);
        
        upload_btn = findViewById(R.id.upload_PDF_button);
        pdf_name = findViewById(R.id.pdf_name);

        //Database
        databaseReference = FirebaseDatabase.getInstance(getString(R.string.database_instance)).getReference("Upload_pdf");
        storageReference = FirebaseStorage.getInstance().getReference();

        //uploading the files or pdf
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFiles();
            }
        });

        //create method
        viewAllFiles();
    }

    private void viewAllFiles() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploads.clear();
                for (DataSnapshot postsnapshot : snapshot.getChildren()) {
                    pdfClass pdfClass = postsnapshot.getValue(com.example.main.UploadPDF_Operation.pdfClass.class);
                    uploads.add(pdfClass);
                }
                pdfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void selectFiles() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF Files..."),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            UploadFiles(data.getData());
        }
    }

    private void UploadFiles(Uri data) {
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference reference = storageReference.child("Uploads/"+System.currentTimeMillis()+".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri uri=uriTask.getResult();

                        pdfClass pdfClass = new pdfClass(pdf_name.getText().toString(),uri.toString());
                        databaseReference.child(databaseReference.push().getKey()).setValue(pdfClass);

                        Toast.makeText(UploadPDFActivity.this, "File Uploaded !!!", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                        pdf_name.setText(null);


                    }

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        double progress=(100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded : "+(int)progress+"%");
                    }
                });
    }

}