package com.example.main.VisitorOperation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main.R;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    final private Context context;
    private List<VisitorDataRetrieve> dataList;
    public static String name2;
    public  static  String visitorName;
    public MyAdapter(Context context, List<VisitorDataRetrieve> dataList) {
        this.context = context;
        this.dataList =  dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
      //  Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
      //  holder.recTitle.setText(dataList.get(position).getDataTitle());
       // holder.recDesc.setText(dataList.get(position).getDataDesc());
      //  holder.recLang.setText(dataList.get(position).getDataLang());
        VisitorDataRetrieve visitorDataRetrieve = dataList.get(position);
        holder.Vname.setText(dataList.get(position).getVis_name());
//        holder.Vemail.setText(dataList.get(position).getVis_email());
        holder.Vnumber.setText(dataList.get(position).  getVis_pnumber());
//        holder.Vqual.setText(dataList.get(position).getVis_qualification());
  //      holder.Vsub.setText(dataList.get(position).getVis_subject());
         name2 = dataList.get(position).getVis_name();



        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    Intent intent = new Intent(context, Detail2Activity.class);
                Intent intent = new Intent(context, Detail4Activity.class);
                intent.putExtra("Name", dataList.get(holder.getAdapterPosition()).getVis_name());
                intent.putExtra("Email Address", dataList.get(holder.getAdapterPosition()).getVis_email());
                intent.putExtra("Phone number", dataList.get(holder.getAdapterPosition()).getVis_pnumber());
                intent.putExtra("Qualification",dataList.get(holder.getAdapterPosition()).getVis_qualification());
                intent.putExtra("Subject", dataList.get(holder.getAdapterPosition()).getVis_subject());
                intent.putExtra("Key", dataList.get(holder.getAdapterPosition()).getVis_subject());

                intent.putExtra("year",dataList.get(holder.getAdapterPosition()).getVis_year());
                intent.putExtra("branch_name",dataList.get(holder.getAdapterPosition()).getVis_Subject_branch());
                intent.putExtra("Account_no",dataList.get(holder.getAdapterPosition()).getVis_acc_no());
                intent.putExtra("Bank_name",dataList.get(holder.getAdapterPosition()).getVis_bank_name());
                intent.putExtra("Bank_branch",dataList.get(holder.getAdapterPosition()).getVis_branch());
                intent.putExtra("IFSC",dataList.get(holder.getAdapterPosition()).getVis_ifsc());


               int clickedPosition = holder.getAdapterPosition();
               // if(clickedPosition !=RecyclerView.NO_POSITION)
              //  {
                    VisitorDataRetrieve clickedVisitor = dataList.get(clickedPosition);
                    visitorName = clickedVisitor.getVis_name();
                //}

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchDataList(ArrayList<VisitorDataRetrieve> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

   // ImageView recImage;
     public TextView Vname, Vemail, Vnumber,Vqual,Vsub;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

       // recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        Vname = itemView.findViewById(R.id.showName);
        Vnumber = itemView.findViewById(R.id.showPnumber);
      //  Vemail = itemView.findViewById(R.id.show)
      //  recDesc = itemView.findViewById(R.id.recDesc);
       // recLang = itemView.findViewById(R.id.recLang);
       // recTitle = itemView.findViewById(R.id.recTitle);
    }
}