package com.example.main.EventOperation;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.main.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<Card> cardList;

    public CardAdapter(VisitorEventActivity mainActivity, List<Card> cardList) {
        this.cardList = cardList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (cardList != null) {
            return cardList.size();
        } else {
            return 0;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Card card = cardList.get(position);

        holder.name2.setText(card.getName());
        holder.subject.setText(card.getSubject());
        holder.class2.setText("Year:"+card.getClass2());
        holder.date2.setText(card.getDate2());
        holder.time2.setText("Time:"+card.getTime2());
        holder.type2.setText("Type:"+card.getType());
        holder.student2.setText("Total student:"+card.getStudent());
        holder.content2.setText("Content:"+card.getContent());




        // Bind the visitor data to your ViewHolder's views
        // ...

        // Set OnClickListener on the accept button
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String visitorName = card.getName();
                String visitorDate = card.getDate2();
                String limiter = "-";
                StringTokenizer date3 = new StringTokenizer(visitorDate,limiter);
                String s1 = date3.nextToken();
                String s2 = date3.nextToken();
                String s3 = date3.nextToken();
                String visitorTime = card.getTime2();
                String visitorMonth = s2;
                String visitorYear = s3;

                DatabaseReference visitorRef = FirebaseDatabase.getInstance("https://main-e041b-default-rtdb.firebaseio.com/").getReference("Visitors").child(visitorName)
                        .child("Calender")
                        .child(visitorDate)
                        .child(visitorTime);

                visitorRef.child("text").setValue("Accepted");
                card.setStatus("Accepted");


                // Remove the accepted visitor from the list or update the list accordingly
                cardList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cardList.size());
                notifyItemInserted(cardList.size() - 1);

                // Perform any additional actions after accepting the visitor
                // ...
            }
        });




        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String visitorName = card.getName();
                String visitorDate = card.getDate2();
                String limiter = "-";
                StringTokenizer date3 = new StringTokenizer(visitorDate,limiter);
                String s1 = date3.nextToken();
                String s2 = date3.nextToken();
                String s3 = date3.nextToken();
                String visitorTime = card.getTime2();
                String visitorMonth = s2;
                String visitorYear = s3;

                DatabaseReference visitorRef = FirebaseDatabase.getInstance("https://main-e041b-default-rtdb.firebaseio.com/").getReference("Visitors").child(visitorName)
                        .child("Calender")
                        .child(visitorDate)
                        .child(visitorTime);

                visitorRef.child("text").setValue("Rejected");
                card.setStatus("Rejected");

                // Remove the accepted visitor from the list or update the list accordingly
             //   cardList.remove(position);
                cardList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cardList.size());
                notifyItemInserted(cardList.size() - 1);


                // Perform any additional actions after accepting the visitor
                // ...
            }
        });
        String status = card.getStatus();
        if (status.equals("Accepted")) {
            holder.accept.setVisibility(View.INVISIBLE);
            holder.reject.setVisibility(View.INVISIBLE);
            holder.name2.setVisibility(View.INVISIBLE);
            holder.subject.setVisibility(View.INVISIBLE);
            holder.class2.setVisibility(View.INVISIBLE);
            holder.date2.setVisibility(View.INVISIBLE);
            holder.time2.setVisibility(View.INVISIBLE);
            holder.type2.setVisibility(View.INVISIBLE);
            holder.content2.setVisibility(View.INVISIBLE);
            holder.student2.setVisibility(View.INVISIBLE);
            cardList.remove(position);


        } else if (status.equals("Rejected")) {
            holder.accept.setVisibility(View.INVISIBLE);
            holder.reject.setVisibility(View.INVISIBLE);
            holder.name2.setVisibility(View.INVISIBLE);
            holder.subject.setVisibility(View.INVISIBLE);
            holder.class2.setVisibility(View.INVISIBLE);
            holder.date2.setVisibility(View.INVISIBLE);
            holder.time2.setVisibility(View.INVISIBLE);
            holder.type2.setVisibility(View.INVISIBLE);
            holder.content2.setVisibility(View.INVISIBLE);
            holder.student2.setVisibility(View.INVISIBLE);
            cardList.remove(position);

        }



    }

    public void notifyItemInserted() {
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView subject, class2, date2, time2, text, name2,type2,student2,content2;
        public Button accept, reject;

        public ViewHolder(View view) {
            super(view);

            name2 = view.findViewById(R.id.vis_name);
            subject = view.findViewById(R.id.Subject);
            class2 = view.findViewById(R.id.Class);
            date2 = view.findViewById(R.id.date1);
            time2 = view.findViewById(R.id.time1);
            type2 = view.findViewById(R.id.type);
            student2 = view.findViewById(R.id.student);
            content2 = view.findViewById(R.id.dcontent);
         //    text = view.findViewById(R.id.text);
            accept = view.findViewById(R.id.accept);
            reject = view.findViewById(R.id.reject);
        }
    }
}
