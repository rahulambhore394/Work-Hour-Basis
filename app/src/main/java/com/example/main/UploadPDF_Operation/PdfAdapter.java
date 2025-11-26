package com.example.main.UploadPDF_Operation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main.R;

import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.PdfViewHolder> {

    private final Context context;
    private final List<pdfClass> pdfList;

    public PdfAdapter(Context context, List<pdfClass> pdfList) {
        this.context = context;
        this.pdfList = pdfList;
    }

    @NonNull
    @Override
    public PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_item, parent, false);
        return new PdfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfViewHolder holder, int position) {
        pdfClass pdf = pdfList.get(position);
        holder.pdfNameTextView.setText(pdf.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("application/pdf");
            intent.setData(Uri.parse(pdf.getUrl()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pdfList.size();
    }

    public static class PdfViewHolder extends RecyclerView.ViewHolder {

        TextView pdfNameTextView;

        public PdfViewHolder(@NonNull View itemView) {
            super(itemView);
            pdfNameTextView = itemView.findViewById(R.id.pdf_name_text_view);
        }
    }
}
