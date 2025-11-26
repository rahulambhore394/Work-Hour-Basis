package com.example.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
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

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityLogSheetActivity extends AppCompatActivity {

    private final List<LogEntry> logEntries = new ArrayList<>();
    private TextView tvTotalTheoryHrs, tvTotalPracticalHrs;
    private TextInputEditText etFacultyName, etSubject, etClass;
    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_sheet);

        initializeViews();
        setupRecyclerView();

        Button btnGeneratePdf = findViewById(R.id.btnGeneratePdf);
        btnGeneratePdf.setOnClickListener(v -> generatePdfWithPermissionCheck());
    }

    private void initializeViews() {
        tvTotalTheoryHrs = findViewById(R.id.tvTotalTheoryHrs);
        tvTotalPracticalHrs = findViewById(R.id.tvTotalPracticalHrs);
        etFacultyName = findViewById(R.id.etFacultyName);
        etSubject = findViewById(R.id.etSubject);
        etClass = findViewById(R.id.etClass);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewLogSheet);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (logEntries.isEmpty()) {
            for (int i = 0; i < 15; i++) {
                logEntries.add(new LogEntry());
            }
        }

        LogSheetAdapter adapter = new LogSheetAdapter(logEntries, this::recalculateTotals);
        recyclerView.setAdapter(adapter);
    }

    private void recalculateTotals() {
        double totalTheory = 0;
        double totalPractical = 0;
        for (LogEntry entry : logEntries) {
            totalTheory += entry.theoryDuration;
            totalPractical += entry.practicalDuration;
        }
        tvTotalTheoryHrs.setText(String.format(Locale.US, "%.1f", totalTheory));
        tvTotalPracticalHrs.setText(String.format(Locale.US, "%.1f", totalPractical));
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

    private boolean validateInputs() {
        String facultyName = etFacultyName.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String className = etClass.getText().toString().trim();

        if (facultyName.isEmpty()) {
            etFacultyName.setError("Faculty Name is required");
            etFacultyName.requestFocus();
            return false;
        }
        if (subject.isEmpty()) {
            etSubject.setError("Subject is required");
            etSubject.requestFocus();
            return false;
        }
        if (className.isEmpty()) {
            etClass.setError("Class is required");
            etClass.requestFocus();
            return false;
        }
        etFacultyName.setError(null);
        etSubject.setError(null);
        etClass.setError(null);
        return true;
    }

    private void createPdf() {
        if (!validateInputs()) {
            Toast.makeText(this, "Please fill all required header fields.", Toast.LENGTH_LONG).show();
            return;
        }

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // Portrait A4
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        drawPdfContent(canvas, page.getInfo().getPageHeight());
        pdfDocument.finishPage(page);
        savePdf(this, pdfDocument, etFacultyName.getText().toString().trim());
    }

    // --- IMPROVED PDF DRAWING METHOD ---
    private void drawPdfContent(Canvas canvas, int pageHeight) {
        // --- Paint Objects ---
        Paint titlePaint = new Paint();
        titlePaint.setTextSize(12);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        Paint deptPaint = new Paint(titlePaint);
        deptPaint.setTextSize(11);

        Paint headerPaint = new Paint();
        headerPaint.setTextSize(10);
        headerPaint.setFakeBoldText(true);
        headerPaint.setColor(Color.BLACK);

        Paint regularPaint = new Paint();
        regularPaint.setTextSize(9);
        regularPaint.setColor(Color.BLACK);

        Paint centeredTextPaint = new Paint(regularPaint);
        centeredTextPaint.setTextAlign(Paint.Align.CENTER);

        Paint strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.BLACK);

        // --- Header ---
        canvas.drawText("SHRI GURU GOBIND SINGHJI INSTITUTE OF", canvas.getWidth() / 2f, 50, titlePaint);
        canvas.drawText("ENGINEERING & TECHNOLOGY, VISHNUPURI, NANDED.", canvas.getWidth() / 2f, 65, titlePaint);

        String deptText = "DEPARTMENT OF INFORMATION TECHNOLOGY";
        float textWidth = deptPaint.measureText(deptText);
        float centerX = canvas.getWidth() / 2f;
        canvas.drawText(deptText, centerX, 90, deptPaint);
        canvas.drawLine(centerX - textWidth / 2, 92, centerX + textWidth / 2, 92, deptPaint);


        // --- Info Section ---
        canvas.drawText("Name of Faculty: " + etFacultyName.getText().toString(), 40, 120, headerPaint);
        canvas.drawText("Subject: " + etSubject.getText().toString(), 40, 140, headerPaint);
        canvas.drawText("Class: " + etClass.getText().toString(), 300, 140, headerPaint);

        // --- Table ---
        int tableTopY = 160;
        int headerHeight = 40;
        int rowHeight = 25;
        float[] colWidths = {40, 80, 60, 60, 80, 60, 60};
        float[] colStarts = new float[8];
        colStarts[0] = 50;
        for (int i = 0; i < colWidths.length; i++) {
            colStarts[i + 1] = colStarts[i] + colWidths[i];
        }
        float tableEnd = colStarts[7];

        int tableBottomY = tableTopY + headerHeight + (16 * rowHeight);
        canvas.drawRect(colStarts[0], tableTopY, tableEnd, tableBottomY, strokePaint);
        canvas.drawLine(colStarts[0], tableTopY + headerHeight, tableEnd, tableTopY + headerHeight, strokePaint);
        for (int i = 1; i < colWidths.length + 1; i++) {
            canvas.drawLine(colStarts[i], tableTopY, colStarts[i], tableBottomY, strokePaint);
        }
        for (int i = 1; i <= 15; i++) {
            canvas.drawLine(colStarts[0], tableTopY + headerHeight + (i * rowHeight), tableEnd, tableTopY + headerHeight + (i * rowHeight), strokePaint);
        }
        canvas.drawLine(colStarts[1], tableTopY + 20, tableEnd, tableTopY + 20, strokePaint);
        canvas.drawLine(colStarts[4], tableTopY, colStarts[4], tableBottomY, strokePaint);

        // Headers
        canvas.drawText("Sr. No.", (colStarts[0] + colStarts[1]) / 2, tableTopY + 25, centeredTextPaint);
        canvas.drawText("Theory", (colStarts[1] + colStarts[4]) / 2, tableTopY + 15, centeredTextPaint);
        canvas.drawText("Practical", (colStarts[4] + colStarts[7]) / 2, tableTopY + 15, centeredTextPaint);
        canvas.drawText("Date", (colStarts[1] + colStarts[2]) / 2, tableTopY + 35, centeredTextPaint);
        canvas.drawText("Time", (colStarts[2] + colStarts[3]) / 2, tableTopY + 35, centeredTextPaint);
        canvas.drawText("Duration (Hrs)", (colStarts[3] + colStarts[4]) / 2, tableTopY + 35, centeredTextPaint);
        canvas.drawText("Date", (colStarts[4] + colStarts[5]) / 2, tableTopY + 35, centeredTextPaint);
        canvas.drawText("Time", (colStarts[5] + colStarts[6]) / 2, tableTopY + 35, centeredTextPaint);
        canvas.drawText("Duration (Hrs)", (colStarts[6] + colStarts[7]) / 2, tableTopY + 35, centeredTextPaint);

        // Data Rows
        double totalTheory = 0;
        double totalPractical = 0;
        int currentY = tableTopY + headerHeight;

        for (int i = 0; i < logEntries.size(); i++) {
            LogEntry entry = logEntries.get(i);
            totalTheory += entry.theoryDuration;
            totalPractical += entry.practicalDuration;
            int textY = currentY + 18;

            canvas.drawText(String.format(Locale.US, "%02d", i + 1), (colStarts[0] + colStarts[1]) / 2, textY, centeredTextPaint);
            canvas.drawText(entry.theoryDate, (colStarts[1] + colStarts[2]) / 2, textY, centeredTextPaint);
            canvas.drawText(entry.theoryTime, (colStarts[2] + colStarts[3]) / 2, textY, centeredTextPaint);
            canvas.drawText(entry.theoryDuration == 0 ? "" : String.valueOf(entry.theoryDuration), (colStarts[3] + colStarts[4]) / 2, textY, centeredTextPaint);
            canvas.drawText(entry.practicalDate, (colStarts[4] + colStarts[5]) / 2, textY, centeredTextPaint);
            canvas.drawText(entry.practicalTime, (colStarts[5] + colStarts[6]) / 2, textY, centeredTextPaint);
            canvas.drawText(entry.practicalDuration == 0 ? "" : String.valueOf(entry.practicalDuration), (colStarts[6] + colStarts[7]) / 2, textY, centeredTextPaint);
            currentY += rowHeight;
        }

        // Totals Row
        canvas.drawText("Total no of Lectures Taken (Hrs)", (colStarts[1] + colStarts[3]) / 2, currentY + 18, centeredTextPaint);
        canvas.drawText(String.format(Locale.US, "%.1f", totalTheory), (colStarts[3] + colStarts[4]) / 2, currentY + 18, centeredTextPaint);
        canvas.drawText("Total no of Practical's Taken (Hrs)", (colStarts[4] + colStarts[6]) / 2, currentY + 18, centeredTextPaint);
        canvas.drawText(String.format(Locale.US, "%.1f", totalPractical), (colStarts[6] + colStarts[7]) / 2, currentY + 18, centeredTextPaint);

        // Signatures
        int signatureY = pageHeight - 60;
        canvas.drawText("Subject Coordinator\nName & Sign.", 100, signatureY, regularPaint);
        canvas.drawText("Head\nInformation Technology Dept.", 450, signatureY, regularPaint);
    }



    private void savePdf(Context context, PdfDocument document, String facultyName) {
        String todayDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String fileName = "LogSheet_" + facultyName.replace(" ", "_") + "_" + todayDate + ".pdf";

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
            // FIX: Show a more prominent error dialog instead of just a toast.
            new AlertDialog.Builder(context)
                    .setTitle("PDF Generation Failed")
                    .setMessage("Could not save the PDF file. Please check storage permissions and try again.\n\nError: " + e.getMessage())
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}



class LogSheetAdapter extends RecyclerView.Adapter<LogSheetAdapter.ViewHolder> {

    private final List<LogEntry> logEntries;
    private final OnDataChangedListener listener;

    public interface OnDataChangedListener {
        void onDataChanged();
    }

    public LogSheetAdapter(List<LogEntry> logEntries, OnDataChangedListener listener) {
        this.logEntries = logEntries;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_log_sheet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(logEntries.get(position));
        holder.tvSrNo.setText(String.format(Locale.US, "%02d", position + 1));
    }

    @Override
    public int getItemCount() { return logEntries.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSrNo;
        EditText etTheoryDate, etTheoryTime, etTheoryDuration;
        EditText etPracticalDate, etPracticalTime, etPracticalDuration;

        ViewHolder(View itemView) {
            super(itemView);
            // FIX: Added all missing findViewById calls
            tvSrNo = itemView.findViewById(R.id.tvSrNo);
            etTheoryDate = itemView.findViewById(R.id.etTheoryDate);
            etTheoryTime = itemView.findViewById(R.id.etTheoryTime);
            etTheoryDuration = itemView.findViewById(R.id.etTheoryDuration);
            etPracticalDate = itemView.findViewById(R.id.etPracticalDate);
            etPracticalTime = itemView.findViewById(R.id.etPracticalTime);
            etPracticalDuration = itemView.findViewById(R.id.etPracticalDuration);

            setupClickableInputs();

            etTheoryDuration.addTextChangedListener(new CustomTextWatcher(entry -> entry.theoryDuration = parseDouble(etTheoryDuration.getText().toString())));
            etPracticalDuration.addTextChangedListener(new CustomTextWatcher(entry -> entry.practicalDuration = parseDouble(etPracticalDuration.getText().toString())));
        }

        void bindData(LogEntry entry) {
            // FIX: Set the text for each field to ensure views are correctly populated/cleared on scroll
            etTheoryDate.setText(entry.theoryDate);
            etTheoryTime.setText(entry.theoryTime);
            etTheoryDuration.setText(entry.theoryDuration == 0 ? "" : String.valueOf(entry.theoryDuration));
            etPracticalDate.setText(entry.practicalDate);
            etPracticalTime.setText(entry.practicalTime);
            etPracticalDuration.setText(entry.practicalDuration == 0 ? "" : String.valueOf(entry.practicalDuration));
        }

        private void setupClickableInputs() {
            makePicker(etTheoryDate);
            makePicker(etTheoryTime);
            makePicker(etPracticalDate);
            makePicker(etPracticalTime);
        }

        private void makePicker(EditText editText) {
            editText.setFocusable(false);
            editText.setClickable(true);
            boolean isDatePicker = editText.getId() == R.id.etTheoryDate || editText.getId() == R.id.etPracticalDate;
            editText.setOnClickListener(v -> {
                if (isDatePicker) {
                    showDatePicker(editText);
                } else {
                    showTimePicker(editText);
                }
            });
        }

        private void showDatePicker(EditText editText) {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(itemView.getContext(), (view, year, month, day) -> {
                String date = String.format(Locale.US, "%02d-%02d-%d", day, month + 1, year);
                editText.setText(date);
                saveDataFromPicker(editText, date);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        }

        private void showTimePicker(EditText editText) {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(itemView.getContext(), (view, hour, minute) -> {
                String time = String.format(Locale.US, "%02d:%02d", hour, minute);
                editText.setText(time);
                saveDataFromPicker(editText, time);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        }

        private void saveDataFromPicker(EditText editText, String data) {
            int pos = getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            LogEntry entry = logEntries.get(pos);
            int id = editText.getId();

            if (id == R.id.etTheoryDate) entry.theoryDate = data;
            else if (id == R.id.etTheoryTime) entry.theoryTime = data;
            else if (id == R.id.etPracticalDate) entry.practicalDate = data;
            else if (id == R.id.etPracticalTime) entry.practicalTime = data;

            // FIX: Notify the activity that data has changed so totals can be updated.
            if (listener != null) listener.onDataChanged();
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
                    setter.update(logEntries.get(pos));
                    if (listener != null) listener.onDataChanged();
                }
            }
        }
    }

    @FunctionalInterface
    interface ValueSetter { void update(LogEntry entry); }
}

class LogEntry {
    String theoryDate = "", theoryTime = "", practicalDate = "", practicalTime = "";
    double theoryDuration = 0, practicalDuration = 0;
}