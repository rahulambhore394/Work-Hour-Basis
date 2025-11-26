package com.example.main.VisitorOperation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main.AdminOperation.CommanActivity;
import com.example.main.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    List<VisitorDataRetrieve> visitorDataRetrieveList;
    MyAdapter myAdapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  //      requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_detail);


        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(DetailActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);


        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();


        visitorDataRetrieveList = new ArrayList<>();
        myAdapter = new MyAdapter(DetailActivity.this, visitorDataRetrieveList);
        recyclerView.setAdapter(myAdapter);

        databaseReference = FirebaseDatabase.getInstance(getString(R.string.database_instance)).getReference("Visitors");
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                visitorDataRetrieveList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    VisitorDataRetrieve dataClass = itemSnapshot.getValue(VisitorDataRetrieve.class);

                    dataClass.setKey(itemSnapshot.getKey());

                    visitorDataRetrieveList.add(dataClass);
                }
               myAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        //to search the present data
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

    }
    public void searchList(String text){
        ArrayList<VisitorDataRetrieve> searchList = new ArrayList<>();
        for (VisitorDataRetrieve dataClass: visitorDataRetrieveList){
            if (dataClass.getVis_name().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        myAdapter.searchDataList(searchList);
    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(getApplicationContext(), CommanActivity.class);
        startActivity(i);

        super.onBackPressed();
    }
}