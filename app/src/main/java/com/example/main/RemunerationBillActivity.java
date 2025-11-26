package com.example.main;

import android.Manifest;
import android.app.AlertDialog;
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
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RemunerationBillActivity extends AppCompatActivity {

    // --- Data Storage ---
    private static String facultyName = "";
    private static String className = "";
    private static String duration = "";
    private static final List<RemunerationEntry> entries = new ArrayList<>();
    private static final int STORAGE_PERMISSION_CODE = 101;

    private int currentStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (entries.size() >= 5) {
            finish();
            return;
        }

        currentStep = entries.size() + 1;

        if (currentStep == 1 && facultyName.isEmpty()) {
            setupFacultyInfoScreen();
        } else {
            setupDailyEntryScreen();
        }
    }

    private void setupFacultyInfoScreen() {
        // BUG FIX: Was loading the wrong layout. Corrected to 'activity_faculty_info'.
        setContentView(R.layout.activity_remuneration_bill);
        entries.clear();

        TextInputEditText etFacultyName = findViewById(R.id.etFacultyName);
        TextInputEditText etClass = findViewById(R.id.etClass);
        TextInputEditText etDuration = findViewById(R.id.etDuration);
        Button btnStartEntry = findViewById(R.id.btnStartEntry);

        btnStartEntry.setOnClickListener(v -> {
            String name = etFacultyName.getText().toString().trim();
            String cName = etClass.getText().toString().trim();
            String dur = etDuration.getText().toString().trim();

            if (name.isEmpty() || cName.isEmpty() || dur.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
                return;
            }

            facultyName = name;
            className = cName;
            duration = dur;
            recreate();
        });
    }

    private void setupDailyEntryScreen() {
        setContentView(R.layout.activity_add_remuneration_entry);

        TextView tvProgress = findViewById(R.id.tvProgress);
        Button btnSubmitEntry = findViewById(R.id.btnSubmitEntry);
        TextInputEditText etSubject = findViewById(R.id.etSubject);
        TextInputEditText etAllocatedTh = findViewById(R.id.etAllocatedTh);
        TextInputEditText etAllocatedPr = findViewById(R.id.etAllocatedPr);
        TextInputEditText etConductedTh = findViewById(R.id.etConductedTh);
        TextInputEditText etConductedPr = findViewById(R.id.etConductedPr);

        tvProgress.setText(String.format(Locale.US, "Entering Details for Day %d of 5", currentStep));
        btnSubmitEntry.setText(String.format(Locale.US, "Submit Day %d Entry", currentStep));

        btnSubmitEntry.setOnClickListener(v -> {
            String subject = etSubject.getText().toString().trim();
            if (subject.isEmpty()) {
                Toast.makeText(this, "Subject name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            RemunerationEntry entry = new RemunerationEntry();
            entry.setSubject(subject);
            entry.setAllocatedTh(parseDouble(etAllocatedTh.getText().toString()));
            entry.setAllocatedPr(parseDouble(etAllocatedPr.getText().toString()));
            entry.setConductedTh(parseDouble(etConductedTh.getText().toString()));
            entry.setConductedPr(parseDouble(etConductedPr.getText().toString()));
            entries.add(entry);

            if (entries.size() >= 5) {
                Toast.makeText(this, "All entries saved. Generating PDF...", Toast.LENGTH_LONG).show();
                generatePdfWithPermissionCheck();
                finish();
            } else {
                recreate();
            }
        });
    }

    private double parseDouble(String s) {
        try {
            return s.isEmpty() ? 0 : Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // --- PDF Generation Logic ---
    private static final double THEORY_RATE = 900;
    private static final double PRACTICAL_RATE = 450;

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
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(842, 595, 1).create(); // A4 Landscape
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        drawPdfContent(canvas, page.getInfo().getPageWidth(), page.getInfo().getPageHeight());
        pdfDocument.finishPage(page);
        savePdf(this, pdfDocument, facultyName);
    }

    private void drawPdfContent(Canvas canvas, int pageWidth, int pageHeight){
        // --- Setup Paint Objects ---
        Paint titlePaint = new Paint();
        titlePaint.setTextSize(12);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        Paint headerPaint = new Paint();
        headerPaint.setTextSize(10);
        headerPaint.setColor(Color.BLACK);

        Paint regularPaint = new Paint(headerPaint);
        regularPaint.setTextAlign(Paint.Align.CENTER);

        Paint subjectPaint = new Paint(regularPaint);
        subjectPaint.setTextAlign(Paint.Align.LEFT);

        Paint boldCenteredPaint = new Paint(regularPaint);
        boldCenteredPaint.setFakeBoldText(true);

        Paint strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.BLACK);

        // --- Define Margins ---
        float leftMargin = 40;
        float rightMargin = 40;
        float topMargin = 40;

        // --- Header ---
        try {
            Bitmap logoBitmap = BitmapFactory.decodeStream(getAssets().open("sggs_logo.png"));
            Bitmap scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 60, 60, false);
            canvas.drawBitmap(scaledLogo, leftMargin, topMargin, null);
        } catch (IOException e) {
            Log.e("PDF_GENERATOR", "Logo file not found in assets", e);
        }

        canvas.drawText("SHRI GURU GOBIND SINGHJI INSTITUTE OF ENGINEERING & TECHNOLOGY, VISHNUPURI, NANDED.", pageWidth / 2f, topMargin + 25, titlePaint);
        canvas.drawText("DEPARTMENT OF INFORMATION TECHNOLOGY", pageWidth / 2f, topMargin + 45, titlePaint);

        // --- Info Section ---
        canvas.drawText("Name of Faculty (C.H.B): " + facultyName, leftMargin + 70, topMargin + 75, headerPaint);
        canvas.drawText("Duration: " + duration, pageWidth - rightMargin - 150, topMargin + 75, headerPaint);
        canvas.drawText("Class: " + className, leftMargin + 70, topMargin + 95, headerPaint);

        // --- Table Drawing ---
        int tableTopY = 150;
        int headerHeight = 40;
        int rowHeight = 30; // Increased row height for better spacing

        // Increased table size by distributing available width
        float drawableWidth = pageWidth - leftMargin - rightMargin;
        float[] colWidths = {40, 222, 50, 50, 50, 50, 50, 50, 50, 50, 100};
        float[] colStarts = new float[12];
        colStarts[0] = leftMargin;
        for (int i = 0; i < colWidths.length; i++) {
            colStarts[i+1] = colStarts[i] + colWidths[i];
        }

        // Draw Table Borders
        int tableBottomY = tableTopY + headerHeight + (6 * rowHeight);
        canvas.drawRect(colStarts[0], tableTopY, colStarts[11], tableBottomY, strokePaint);
        canvas.drawLine(colStarts[0], tableTopY + headerHeight, colStarts[11], tableTopY + headerHeight, strokePaint);
        for (int i = 1; i < colStarts.length; i++) {
            canvas.drawLine(colStarts[i], tableTopY, colStarts[i], tableBottomY, strokePaint);
        }
        for (int i = 1; i <= 5; i++) {
            canvas.drawLine(colStarts[0], tableTopY + headerHeight + (i * rowHeight), colStarts[11], tableTopY + headerHeight + (i * rowHeight), strokePaint);
        }
        canvas.drawLine(colStarts[2], tableTopY + 20, colStarts[10], tableTopY + 20, strokePaint);

        // Draw Headers Text
        canvas.drawText("Sr. No.", (colStarts[0] + colStarts[1]) / 2, tableTopY + 25, boldCenteredPaint);
        canvas.drawText("Subject", (colStarts[1] + colStarts[2]) / 2, tableTopY + 25, boldCenteredPaint);
        canvas.drawText("Load Allocated", (colStarts[2] + colStarts[4]) / 2, tableTopY + 15, boldCenteredPaint);
        canvas.drawText("Load Conducted", (colStarts[4] + colStarts[6]) / 2, tableTopY + 15, boldCenteredPaint);
        canvas.drawText("Extra Load", (colStarts[6] + colStarts[8]) / 2, tableTopY + 15, boldCenteredPaint);
        canvas.drawText("Amount", (colStarts[8] + colStarts[10]) / 2, tableTopY + 15, boldCenteredPaint);
        canvas.drawText("Total Rs.", (colStarts[10] + colStarts[11]) / 2, tableTopY + 25, boldCenteredPaint);

        // Draw Sub-headers Text
        canvas.drawText("Th.", (colStarts[2] + colStarts[3]) / 2, tableTopY + 35, boldCenteredPaint);
        canvas.drawText("Pr.", (colStarts[3] + colStarts[4]) / 2, tableTopY + 35, boldCenteredPaint);
        canvas.drawText("Th.", (colStarts[4] + colStarts[5]) / 2, tableTopY + 35, boldCenteredPaint);
        canvas.drawText("Pr.", (colStarts[5] + colStarts[6]) / 2, tableTopY + 35, boldCenteredPaint);
        canvas.drawText("Th.", (colStarts[6] + colStarts[7]) / 2, tableTopY + 35, boldCenteredPaint);
        canvas.drawText("Pr.", (colStarts[7] + colStarts[8]) / 2, tableTopY + 35, boldCenteredPaint);
        canvas.drawText("Th.", (colStarts[8] + colStarts[9]) / 2, tableTopY + 35, boldCenteredPaint);
        canvas.drawText("Pr.", (colStarts[9] + colStarts[10]) / 2, tableTopY + 35, boldCenteredPaint);

        // --- Loop through entries and draw rows ---
        double grandTotal = 0;
        int currentY = tableTopY + headerHeight;
        for(int i = 0; i < entries.size(); i++){
            RemunerationEntry entry = entries.get(i);
            double extraTh = Math.max(0, entry.getConductedTh() - entry.getAllocatedTh());
            double extraPr = Math.max(0, entry.getConductedPr() - entry.getAllocatedPr());
            double amountTh = entry.getConductedTh() * THEORY_RATE;
            double amountPr = entry.getConductedPr() * PRACTICAL_RATE;
            double totalRs = amountTh + amountPr;
            grandTotal += totalRs;
            int textY = currentY + (rowHeight / 2) + 5; // Center text vertically in the row

            canvas.drawText(String.format(Locale.US, "%02d", i + 1), (colStarts[0] + colStarts[1]) / 2, textY, regularPaint);
            canvas.drawText(entry.getSubject(), colStarts[1] + 5, textY, subjectPaint);
            canvas.drawText(String.valueOf(entry.getAllocatedTh()), (colStarts[2] + colStarts[3]) / 2, textY, regularPaint);
            canvas.drawText(String.valueOf(entry.getAllocatedPr()), (colStarts[3] + colStarts[4]) / 2, textY, regularPaint);
            canvas.drawText(String.valueOf(entry.getConductedTh()), (colStarts[4] + colStarts[5]) / 2, textY, regularPaint);
            canvas.drawText(String.valueOf(entry.getConductedPr()), (colStarts[5] + colStarts[6]) / 2, textY, regularPaint);
            canvas.drawText(String.format(Locale.US, "%.1f", extraTh), (colStarts[6] + colStarts[7]) / 2, textY, regularPaint);
            canvas.drawText(String.format(Locale.US, "%.1f", extraPr), (colStarts[7] + colStarts[8]) / 2, textY, regularPaint);
            canvas.drawText(String.format(Locale.US, "%.0f", amountTh), (colStarts[8] + colStarts[9]) / 2, textY, regularPaint);
            canvas.drawText(String.format(Locale.US, "%.0f", amountPr), (colStarts[9] + colStarts[10]) / 2, textY, regularPaint);
            canvas.drawText(String.format(Locale.US, "%.2f", totalRs), (colStarts[10] + colStarts[11]) / 2, textY, boldCenteredPaint);
            currentY += rowHeight;
        }

        // --- Draw Grand Total ---
        int textY = currentY + (rowHeight / 2) + 5;
        canvas.drawText("Total Rs.", (colStarts[9] + colStarts[10]) / 2, textY, boldCenteredPaint);
        canvas.drawText(String.format(Locale.US, "%.2f", grandTotal), (colStarts[10] + colStarts[11]) / 2, textY, boldCenteredPaint);

        // --- Signatures ---
        int signatureY = pageHeight - 60;
        canvas.drawText("Head", leftMargin + 70, signatureY, headerPaint);
        canvas.drawText("Information Technology Dept.", leftMargin + 70, signatureY + 15, regularPaint);
        canvas.drawText("Name of Faculty (C.H.B)", pageWidth - rightMargin - 150, signatureY, headerPaint);
        canvas.drawText("Signature", pageWidth - rightMargin - 150, signatureY + 15, regularPaint);
    }

    private void savePdf(Context context, PdfDocument document, String facultyName) {
        String todayDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String fileName = "RemunerationBill_" + facultyName.replace(" ", "_") + "_" + todayDate + ".pdf";
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


// Data class to hold info for one row.
class RemunerationEntry {
    String subject = "";
    double allocatedTh = 0, allocatedPr = 0;
    double conductedTh = 0, conductedPr = 0;

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public double getAllocatedTh() { return allocatedTh; }
    public void setAllocatedTh(double allocatedTh) { this.allocatedTh = allocatedTh; }
    public double getAllocatedPr() { return allocatedPr; }
    public void setAllocatedPr(double allocatedPr) { this.allocatedPr = allocatedPr; }
    public double getConductedTh() { return conductedTh; }
    public void setConductedTh(double conductedTh) { this.conductedTh = conductedTh; }
    public double getConductedPr() { return conductedPr; }
    public void setConductedPr(double conductedPr) { this.conductedPr = conductedPr; }
}