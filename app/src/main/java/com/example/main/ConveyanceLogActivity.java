package com.example.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConveyanceLogActivity extends AppCompatActivity {

    private final List<ConveyanceEntry> conveyanceEntries = new ArrayList<>();
    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conveyance_log);

        TextView tvDeptTitle = findViewById(R.id.tvDeptTitle);
        TextView tvSubTitle = findViewById(R.id.tvSubTitle);
        tvDeptTitle.setPaintFlags(tvDeptTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvSubTitle.setPaintFlags(tvSubTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        setupRecyclerView();

        Button btnGeneratePdf = findViewById(R.id.btnGeneratePdf);
        btnGeneratePdf.setOnClickListener(v -> generatePdfWithPermissionCheck());
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewConveyance);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (conveyanceEntries.isEmpty()) {
            for (int i = 0; i < 24; i++) {
                conveyanceEntries.add(new ConveyanceEntry());
            }
        }

        ConveyanceAdapter adapter = new ConveyanceAdapter(conveyanceEntries);
        recyclerView.setAdapter(adapter);
    }

    private void generatePdfWithPermissionCheck() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            createPdf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createPdf();
            } else {
                Toast.makeText(this, "Permission denied. Cannot create PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createPdf() {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // Portrait A4
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        drawPdfContent(page.getCanvas(), page.getInfo().getPageHeight());
        pdfDocument.finishPage(page);
        savePdf(this, pdfDocument);
    }

    private void drawPdfContent(Canvas canvas, int pageHeight) {
        // --- Paint Objects ---
        Paint titlePaint = new Paint();
        titlePaint.setTextSize(12);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        Paint deptPaint = new Paint(titlePaint);
        deptPaint.setTextSize(10);

        Paint regularPaint = new Paint();
        regularPaint.setTextSize(10);
        regularPaint.setColor(Color.BLACK);

        Paint centeredTextPaint = new Paint(regularPaint);
        centeredTextPaint.setTextAlign(Paint.Align.CENTER);

        Paint strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.BLACK);

        // --- Header ---
        // NEW: Draw the logo
        try {
            Bitmap logoBitmap = BitmapFactory.decodeStream(getAssets().open("sggs_logo.png")); // Ensure logo is in assets folder
            Bitmap scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 50, 50, false);
            canvas.drawBitmap(scaledLogo, 60, 45, null);
        } catch (IOException e) {
            Log.e("PDF_GENERATOR", "Logo file not found", e);
        }

        canvas.drawText("SHRI GURU GOBIND SINGHJI INSTITUTE OF", canvas.getWidth() / 2f, 60, titlePaint);
        canvas.drawText("ENGINEERING & TECHNOLOGY, VISHNUPURI, NANDED.", canvas.getWidth() / 2f, 78, titlePaint);

        String deptText = "DEPARTMENT OF INFORMATION TECHNOLOGY";
        float textWidth = deptPaint.measureText(deptText);
        float centerX = canvas.getWidth() / 2f;
        canvas.drawText(deptText, centerX, 110, deptPaint);
        canvas.drawLine(centerX - textWidth / 2, 112, centerX + textWidth / 2, 112, deptPaint);

        String subText = "Local Conveyance";
        textWidth = deptPaint.measureText(subText);
        canvas.drawText(subText, centerX, 130, deptPaint);
        canvas.drawLine(centerX - textWidth / 2, 132, centerX + textWidth / 2, 132, deptPaint);

        // --- Table ---
        int tableTopY = 160;
        int headerHeight = 25;
        int rowHeight = 22; // Slightly smaller row height to fit all 24 rows comfortably
        float[] colStarts = {60, 110, 260, 360};
        float tableEnd = 535;

        int tableBottomY = tableTopY + headerHeight + (25 * rowHeight); // 24 rows + 1 total row
        canvas.drawRect(colStarts[0], tableTopY, tableEnd, tableBottomY, strokePaint);
        canvas.drawLine(colStarts[0], tableTopY + headerHeight, tableEnd, tableTopY + headerHeight, strokePaint);
        for (int i = 1; i < colStarts.length; i++) {
            canvas.drawLine(colStarts[i], tableTopY, colStarts[i], tableBottomY, strokePaint);
        }
        for (int i = 1; i <= 24; i++) {
            canvas.drawLine(colStarts[0], tableTopY + headerHeight + (i * rowHeight), tableEnd, tableTopY + headerHeight + (i * rowHeight), strokePaint);
        }

        // Headers
        canvas.drawText("Sr. No.", (colStarts[0] + colStarts[1]) / 2, tableTopY + 17, centeredTextPaint);
        canvas.drawText("Date", (colStarts[1] + colStarts[2]) / 2, tableTopY + 17, centeredTextPaint);
        canvas.drawText("Amount", (colStarts[2] + colStarts[3]) / 2, tableTopY + 17, centeredTextPaint);
        canvas.drawText("Details", (colStarts[3] + tableEnd) / 2, tableTopY + 17, centeredTextPaint);

        // Data Rows
        double grandTotal = 0;
        int currentY = tableTopY + headerHeight;
        for (int i = 0; i < conveyanceEntries.size(); i++) {
            ConveyanceEntry entry = conveyanceEntries.get(i);
            grandTotal += entry.amount;
            int textY = currentY + 16; // Adjusted for new row height

            canvas.drawText(String.valueOf(i + 1), (colStarts[0] + colStarts[1]) / 2, textY, centeredTextPaint);
            canvas.drawText(entry.date, (colStarts[1] + colStarts[2]) / 2, textY, centeredTextPaint);
            canvas.drawText(entry.amount == 0 ? "" : String.format(Locale.US, "%.2f", entry.amount), (colStarts[2] + colStarts[3]) / 2, textY, centeredTextPaint);
            canvas.drawText(entry.details, colStarts[3] + 5, textY, regularPaint);
            currentY += rowHeight;
        }

        // Total Row
        canvas.drawText("Total", (colStarts[1] + colStarts[2]) / 2, currentY + 16, centeredTextPaint);
        canvas.drawText(String.format(Locale.US, "%.2f", grandTotal), (colStarts[2] + colStarts[3]) / 2, currentY + 16, centeredTextPaint);

        // Signatures
        int signatureY = pageHeight - 60;
        canvas.drawText("Subject Coordinator\nName & Sign.", 80, signatureY, regularPaint);
        canvas.drawText("Head\nInformation Technology Dept.", 430, signatureY, regularPaint);
    }

    private void savePdf(Context context, PdfDocument document) {
        String todayDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String fileName = "ConveyanceLog_" + todayDate + ".pdf";

        try {
            OutputStream fos;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);
                Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                fos = context.getContentResolver().openOutputStream(uri);
            } else {
                java.io.File file = new java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName);
                fos = new java.io.FileOutputStream(file);
            }

            if (fos != null) {
                document.writeTo(fos);
                fos.close();
                Toast.makeText(context, "PDF saved successfully", Toast.LENGTH_LONG).show();
            }
            document.close();
        } catch (IOException e) {
            Log.e("PDF_GENERATOR", "Error saving PDF: ", e);
            new AlertDialog.Builder(context)
                    .setTitle("PDF Generation Failed")
                    .setMessage("Could not save the PDF file.\nError: " + e.getMessage())
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}


class ConveyanceAdapter extends RecyclerView.Adapter<ConveyanceAdapter.ViewHolder> {

    private final List<ConveyanceEntry> conveyanceEntries;

    public ConveyanceAdapter(List<ConveyanceEntry> logEntries) {
        this.conveyanceEntries = logEntries;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_conveyance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvSrNo.setText(String.format(Locale.US, "%d.", position + 1));
        holder.bindData(conveyanceEntries.get(position));
    }

    @Override
    public int getItemCount() { return conveyanceEntries.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSrNo;
        EditText etDate, etAmount, etDetails;

        ViewHolder(View itemView) {
            super(itemView);
            tvSrNo = itemView.findViewById(R.id.tvSrNo);
            etDate = itemView.findViewById(R.id.etDate);
            etAmount = itemView.findViewById(R.id.etAmount);
            etDetails = itemView.findViewById(R.id.etDetails);

            setupClickableInputs();

            etAmount.addTextChangedListener(new CustomTextWatcher(entry -> entry.amount = parseDouble(etAmount.getText().toString())));
            etDetails.addTextChangedListener(new CustomTextWatcher(entry -> entry.details = etDetails.getText().toString()));
        }

        void bindData(ConveyanceEntry entry) {
            etDate.setText(entry.date);
            etAmount.setText(entry.amount == 0 ? "" : String.valueOf(entry.amount));
            etDetails.setText(entry.details);
        }

        private void setupClickableInputs() {
            etDate.setFocusable(false);
            etDate.setClickable(true);
            etDate.setOnClickListener(v -> showDatePicker(etDate));
        }

        private void showDatePicker(EditText editText) {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(itemView.getContext(), (view, year, month, day) -> {
                String date = String.format(Locale.US, "%02d-%02d-%d", day, month + 1, year);
                editText.setText(date);
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    conveyanceEntries.get(pos).date = date;
                }
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        }

        private double parseDouble(String s) {
            try { return s.isEmpty() ? 0 : Double.parseDouble(s); }
            catch (NumberFormatException e) { return 0; }
        }

        private class CustomTextWatcher implements TextWatcher {
            private final ValueSetter setter;
            CustomTextWatcher(ValueSetter setter) { this.setter = setter; }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    setter.update(conveyanceEntries.get(pos));
                }
            }
        }
    }

    @FunctionalInterface
    interface ValueSetter { void update(ConveyanceEntry entry); }
}

/**
 * Data model for a single row in the conveyance log.
 */
class ConveyanceEntry {
    String date = "";
    double amount = 0;
    String details = "";
}