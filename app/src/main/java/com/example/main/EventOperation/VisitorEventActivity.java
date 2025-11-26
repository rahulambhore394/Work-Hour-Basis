package com.example.main.EventOperation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.main.R;
import com.example.main.VisitorOperation.MyAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class VisitorEventActivity extends AppCompatActivity {

    public static String submissionId;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private List<Card> card;
    private Card card1, card2;
    private CardAdapter cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_event);

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        card = new ArrayList<>();


        FirebaseDatabase database = FirebaseDatabase.getInstance(getString(R.string.database_instance));
        DatabaseReference myRef = database.getReference("Visitors");
        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {
                    String visitorName = nameSnapshot.getKey();
                    System.out.println("name-----" + visitorName);

                    // Retrieve the data for the visitor with the accepted text and the specified name
                    for (DataSnapshot calendarSnapshot : nameSnapshot.getChildren()) {
                        for (DataSnapshot dateSnapshot : calendarSnapshot.getChildren()) {
                            for (DataSnapshot timeSnapshot : dateSnapshot.getChildren()) {

                                String text = timeSnapshot.child("text").getValue(String.class);

                                if (timeSnapshot.hasChild("text") && text.equals("Pending")) {
                                    String subject2 = timeSnapshot.child("subject").getValue(String.class);
                                    String class2 = timeSnapshot.child("class2").getValue(String.class);
                                    String date2 = timeSnapshot.child("date2").getValue(String.class);
                                    String type2 = timeSnapshot.child("calender_t").getValue(String.class);
                                    String Student2 = timeSnapshot.child("calender_S").getValue(String.class);
                                    String content2 = timeSnapshot.child("cont").getValue(String.class);
                                    String time2 = timeSnapshot.child("time2").getValue(String.class);

                                    System.out.println(subject2 + "--" + class2 + "--" + date2 + "--" + time2 + "\n");
                                    card1 = new Card(visitorName, subject2, class2, date2, time2, "Pending", type2, content2, Student2);
                                    Card.Constant_Name = visitorName;
                                    card.add(card1);

                                    cardAdapter = new CardAdapter(VisitorEventActivity.this, card);
                                    recyclerView.setAdapter(cardAdapter);
                                    cardAdapter.notifyItemInserted();
                                }
                            }


                        }
                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}