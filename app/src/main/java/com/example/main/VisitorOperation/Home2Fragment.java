package com.example.main.VisitorOperation;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import com.example.main.VisitorOperation.MyAdapter.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.main.EventOperation.Card;
import com.example.main.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.StringTokenizer;

//public class Home2Fragment extends Fragment {
//
//    TextView approvedCount, rejectedCount, pendingCount;
//    String visitor_name;
//    Card card;
//    MyViewHolder myViewHolder;
//    // Get a reference to your Firebase database
//    VisitorDataRetrieve visitorDataRetrieve;
//    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mainproject-e095f-default-rtdb.firebaseio.com/");
//    DatabaseReference myRef = database.getReference("Visitors");
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_home2, container, false);
//        visitor_name = MyAdapter.visitorName;
//        Context context = getContext();
//        approvedCount = view.findViewById(R.id.approvedNo);
//        rejectedCount = view.findViewById(R.id.rejectedNo);
//        pendingCount = view.findViewById(R.id.pendingNo);
//
//
//        myRef.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int accpeted_count = 0;
//                int rejected_count = 0;
//                int pending_count = 0;
//
//                for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {
//                    String visitorName = nameSnapshot.getKey();
//                    System.out.println("name-----" + visitorName);
//                    System.out.println("name2-------" + visitor_name);
//                    if (visitorName.equals(visitor_name)) {
//                        // Retrieve the data for the visitor with the accepted text and the specified name
//                        for (DataSnapshot calendarSnapshot : nameSnapshot.getChildren()) {
//                            for (DataSnapshot dateSnapshot : calendarSnapshot.getChildren()) {
//                                for (DataSnapshot timeSnapshot : dateSnapshot.getChildren()) {
//                                    // Retrieve the specific data for the accepted visitor
//                                    String text = timeSnapshot.child("text").getValue(String.class);
//                                    if (text != null && text.equals("Accepted")) {
//                                        accpeted_count++;
//                                        System.out.println("Count ---" + accpeted_count);
//                                    }
//                                    if (text != null && text.equals("Pending")) {
//                                        pending_count++;
//                                        System.out.println("Count ---" + pending_count);
//                                    }
//                                    if (text != null && text.equals("Rejected")) {
//                                        rejected_count++;
//                                        System.out.println("Count ---" + rejected_count);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                // Log.d(TAG, "Number of accepted texts: " + accpeted_count);
//                System.out.println("Number of accepted texts: " + accpeted_count);
//                System.out.println("Number of pending texts: " + pending_count);
//                System.out.println("Number of rejected texts: " + rejected_count);
//
//                approvedCount.setText("" + accpeted_count);
//                rejectedCount.setText(""+rejected_count);
//                pendingCount.setText(""+pending_count);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle any errors
//            }
//        });
//
//        return view;
//    }
//}

public class Home2Fragment extends Fragment {

    TextView approvedCount, rejectedCount, pendingCount;
    String visitor_name;
    Card card;
    MyViewHolder myViewHolder;
    // Get a reference to your Firebase database
    VisitorDataRetrieve visitorDataRetrieve;
    FirebaseDatabase database;
    DatabaseReference myRef;

    public Toolbar toolbar;
    private LayoutInflater inflater;
    @Nullable
    private ViewGroup container;
    @Nullable
    private Bundle savedInstanceState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home2, container, false);
        visitor_name = MyAdapter.visitorName;
        Context context = getContext();
        approvedCount = view.findViewById(R.id.approvedNo);
        rejectedCount = view.findViewById(R.id.rejectedNo);
        pendingCount = view.findViewById(R.id.pendingNo);

        database = FirebaseDatabase.getInstance(getString(R.string.database_instance));
        myRef = database.getReference("Visitors");

        // Assuming you have references to the necessary UI elements and Firebase database reference.

// Step 3: Retrieve the selected menu option


// Step 4: Count the accepted, rejected, and pending data

        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int accpeted_count = 0;
                int rejected_count = 0;
                int pending_count = 0;

                for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {
                    String visitorName = nameSnapshot.getKey();
                    System.out.println("name-----" + visitorName);
                    System.out.println("name2-------" + visitor_name);
                    assert visitorName != null;
                    if (visitorName.equals(visitor_name)) {
                        // Retrieve the data for the visitor with the accepted text and the specified name
                        for (DataSnapshot calendarSnapshot : nameSnapshot.getChildren()) {



                                    for (DataSnapshot dateSnapshot : calendarSnapshot.getChildren()) {

                                        for (DataSnapshot timeSnapshot : dateSnapshot.getChildren()) {
                                            // Retrieve the specific data for the accepted visitor

                                            String date2 = timeSnapshot.child("date2").getValue(String.class);

                                            String text = timeSnapshot.child("text").getValue(String.class);

                                            //              FirebaseDatabase.getInstance("https://mainproject-e095f-default-rtdb.firebaseio.com/").getReference("Visitors").child("Calender1").child(year).child(month).child(visitorName).child(text);
                                            if (text != null && text.equals("Accepted")) {
                                                accpeted_count++;
                                                System.out.println("Count ---" + accpeted_count);
                                            }
                                            if (text != null && text.equals("Pending")) {
                                                pending_count++;
                                                System.out.println("Count ---" + pending_count);
                                            }
                                            if (text != null && text.equals("Rejected")) {
                                                rejected_count++;
                                                System.out.println("Count ---" + rejected_count);
                                            }
                                        }

                            }
                        }
                    }
                }

                System.out.println("Number of accepted texts: " + accpeted_count);
                System.out.println("Number of pending texts: " + pending_count);
                System.out.println("Number of rejected texts: " + rejected_count);

                approvedCount.setText("" + accpeted_count);
                rejectedCount.setText("" + rejected_count);
                pendingCount.setText("" + pending_count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });

        return view;
    }


}
