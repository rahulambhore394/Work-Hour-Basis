package com.example.main;

import android.Manifest;
import android.content.ContentValues;
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
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GuestFacultyDetails extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private TextInputEditText etFacultyName, etFacultyAddress, etLectureHours, etLectureClass,
            etPracticalHours, etPracticalClass, etConveyanceDays, etPaperSetHours,
            etBankName, etBranch, etIfscCode, etAccountNo, etMobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_faculty_details);

        initializeViews();

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> {
            if (checkPermission()) {
                generatePdf();
            } else {
                requestPermission();
            }
        });
    }

    private void initializeViews() {
        etFacultyName = findViewById(R.id.etFacultyName);
        etFacultyAddress = findViewById(R.id.etFacultyAddress);
        etLectureHours = findViewById(R.id.etLectureHours);
        etPracticalHours = findViewById(R.id.etPracticalHours);
        etConveyanceDays = findViewById(R.id.etConveyanceDays);
        etPaperSetHours = findViewById(R.id.etPaperSetHours);
        etBankName = findViewById(R.id.etBankName);
        etBranch = findViewById(R.id.etBranch);
        etIfscCode = findViewById(R.id.etIfscCode);
        etAccountNo = findViewById(R.id.etAccountNo);
        etMobileNo = findViewById(R.id.etMobileNo);
        etLectureClass = findViewById(R.id.etLectureClass);
        etPracticalClass = findViewById(R.id.etPracticalClass);
    }

    private void generatePdf() {
        String lectureClass = etLectureClass.getText().toString().trim();
        String practicalClass = etPracticalClass.getText().toString().trim();
        String facultyName = etFacultyName.getText().toString().trim();
        String facultyAddress = etFacultyAddress.getText().toString().trim();
        String bankName = etBankName.getText().toString().trim();
        String branch = etBranch.getText().toString().trim();
        String ifscCode = etIfscCode.getText().toString().trim();
        String accountNo = etAccountNo.getText().toString().trim();
        String mobileNo = etMobileNo.getText().toString().trim();

        if (facultyName.isEmpty() || accountNo.isEmpty()) {
            Toast.makeText(this, "Faculty Name and Account No. are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        double lectureHours = Double.parseDouble(etLectureHours.getText().toString().isEmpty() ? "0" : etLectureHours.getText().toString());
        double practicalHours = Double.parseDouble(etPracticalHours.getText().toString().isEmpty() ? "0" : etPracticalHours.getText().toString());
        int conveyanceDays = Integer.parseInt(etConveyanceDays.getText().toString().isEmpty() ? "0" : etConveyanceDays.getText().toString());
        double paperSetHours = Double.parseDouble(etPaperSetHours.getText().toString().isEmpty() ? "0" : etPaperSetHours.getText().toString());

        double totalA = lectureHours * 900;
        double totalB = practicalHours * 450;
        double totalC = conveyanceDays * 100;
        double totalD = paperSetHours * 50;
        double grandTotal = totalA + totalB + totalC + totalD;

        PdfDocument pdfDocument = new PdfDocument();
        int pageWidth = 595;
        int pageHeight = 842;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE); // Set paint to stroke for drawing boxes

        Paint titlePaint = new Paint();
        titlePaint.setFakeBoldText(true);
        titlePaint.setTextSize(12);

        Paint headingPaint = new Paint();
        headingPaint.setFakeBoldText(true);
        headingPaint.setTextSize(10);

        Paint normalPaint = new Paint();
        normalPaint.setTextSize(10);

        Paint dataPaint = new Paint();
        dataPaint.setTextSize(10);
        dataPaint.setFakeBoldText(true);

        int startX = 40;
        int startY = 50;
        int currentY = startY;

        titlePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("SHRI GURU GOBIND SINGHJI INSTITUTE OF ENGINEERING & TECHNOLOGY", pageWidth / 2f, currentY, titlePaint);
        currentY += 15;
        normalPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("VISHNUPURI, NANDED - 431 606.", pageWidth / 2f, currentY, normalPaint);
        currentY += 25;
        canvas.drawText("UG/PG Classes", pageWidth / 2f, currentY, normalPaint);
        currentY += 12;
        canvas.drawText("(To be submitted in duplicate)", pageWidth / 2f, currentY, normalPaint);

        normalPaint.setTextAlign(Paint.Align.LEFT);
        currentY += 40;

        canvas.drawText("Name of the Guest Faculty:-", startX, currentY, normalPaint);
        canvas.drawText(facultyName.toUpperCase(), startX + 150, currentY, dataPaint);
        currentY += 20;
        canvas.drawText("Address of the Guest Faculty:-", startX, currentY, normalPaint);
        String[] addressLines = facultyAddress.split("\n");
        for (String line : addressLines) {
            canvas.drawText(line, startX + 150, currentY, dataPaint);
            currentY += 15;
        }

        currentY += 20;

        String todayDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        canvas.drawText("No. of Lecture periods of 60 minutes taken during the period from Date:- " + todayDate, startX, currentY, normalPaint);
        currentY += 15;
        canvas.drawText("of " + lectureClass + ": ( " + lectureHours + " Hours)", startX, currentY, normalPaint);
        canvas.drawText(String.format(Locale.US, "Total Rs %.2f /- (A)", totalA), 400, currentY, normalPaint);
        currentY += 15;
        canvas.drawText("Remuneration claimed for lectures at (Rs. 900/- Per hr.)", startX, currentY, normalPaint);
        currentY += 30;

        canvas.drawText("No. of Practical's of 02 hours taken during the period from Date:- " + todayDate, startX, currentY, normalPaint);
        currentY += 15;
        canvas.drawText("of " + practicalClass + ": ( " + practicalHours + " Hours)", startX, currentY, normalPaint);
        canvas.drawText(String.format(Locale.US, "Total Rs %.2f /- (B)", totalB), 400, currentY, normalPaint);
        currentY += 15;
        canvas.drawText("Remuneration claimed for Practical at Rs. 450/- Per hr.", startX, currentY, normalPaint);
        currentY += 30;

        canvas.drawText("Local Conveyance @ Rs. 100/- Per day " + conveyanceDays + " Days", startX, currentY, normalPaint);
        canvas.drawText(String.format(Locale.US, "Total Rs %.2f /- (C)", totalC), 400, currentY, normalPaint);
        currentY += 20;

        canvas.drawText("Paper set @ Rs. 50/- Per hr. " + paperSetHours + " Hours", startX, currentY, normalPaint);
        canvas.drawText(String.format(Locale.US, "Total Rs %.2f /- (D)", totalD), 400, currentY, normalPaint);
        currentY += 30;

        paint.setStyle(Paint.Style.FILL_AND_STROKE); // Reset paint for lines
        canvas.drawLine(startX, currentY, pageWidth - startX, currentY, paint);
        currentY += 20;

        canvas.drawText("Total Remuneration claimed (A)+(B)+(C)+(D) Rs. = " + String.format(Locale.US, "%.2f", grandTotal), startX, currentY, headingPaint);
        currentY += 20;

        // --- MODIFIED: Convert total to words and draw it ---
        String amountInWords = NumberToWordsConverter.convert((long) grandTotal);
        canvas.drawText("(In Words Rs. " + amountInWords + " Only)", startX, currentY, normalPaint);

        currentY += 80;
        canvas.drawText("Signature of the Guest faculty", startX + 50, currentY, normalPaint);
        canvas.drawText("Coordinator", 400, currentY, normalPaint);
        currentY += 60;
        canvas.drawLine(startX, currentY, pageWidth - startX, currentY, paint);

        // --- MODIFIED: New, clearer logic to draw the bank details table ---
        currentY += 15;
        canvas.drawText("Guest faculty Bank Details", startX, currentY, headingPaint);
        currentY += 15;

        paint.setStyle(Paint.Style.STROKE); // Set paint to draw box outlines
        int tableTop = currentY;
        int rowHeight = 25;
        int headerY = tableTop + 15;
        int dataY = tableTop + rowHeight + 15;
        int tableBottom = tableTop + (2 * rowHeight);
        int tableEnd = pageWidth - startX;

        int[] colStarts = {startX, startX + 105, startX + 210, startX + 315, startX + 420};
        int tableWidth = tableEnd - startX;

        // Draw table box and lines
        canvas.drawRect(startX, tableTop, tableEnd, tableBottom, paint); // Outer box
        canvas.drawLine(startX, tableTop + rowHeight, tableEnd, tableTop + rowHeight, paint); // Horizontal line
        for (int i = 1; i < 5; i++) { // Vertical lines
            canvas.drawLine(colStarts[i], tableTop, colStarts[i], tableBottom, paint);
        }

        paint.setStyle(Paint.Style.FILL); // Reset paint to draw text
        // Draw Headers
        canvas.drawText("Bank Name", colStarts[0] + 5, headerY, normalPaint);
        canvas.drawText("Branch", colStarts[1] + 5, headerY, normalPaint);
        canvas.drawText("IFS Code", colStarts[2] + 5, headerY, normalPaint);
        canvas.drawText("Account No.", colStarts[3] + 5, headerY, normalPaint);
        canvas.drawText("Mobile No.", colStarts[4] + 5, headerY, normalPaint);

        // Draw Data
        canvas.drawText(bankName, colStarts[0] + 5, dataY, dataPaint);
        canvas.drawText(branch, colStarts[1] + 5, dataY, dataPaint);
        canvas.drawText(ifscCode, colStarts[2] + 5, dataY, dataPaint);
        canvas.drawText(accountNo, colStarts[3] + 5, dataY, dataPaint);
        canvas.drawText(mobileNo, colStarts[4] + 5, dataY, dataPaint);

        currentY = tableBottom + 30; // Position footer after the table

        // --- END OF MODIFIED BANK DETAILS SECTION ---

        canvas.drawText("Account Section please Transfer Amount Rs. " + String.format(Locale.US, "%.2f", grandTotal), startX, currentY, normalPaint);
        currentY += 20;
        canvas.drawText("To above A/c. no. immediately.", startX, currentY, normalPaint);
        currentY += 60;
        canvas.drawText("Head", 400, currentY, normalPaint);
        currentY += 15;
        canvas.drawText("Dept. of Information Technology", 400, currentY, normalPaint);

        pdfDocument.finishPage(page);

        String fileName = "SGGSIET_Claim_" + facultyName.replace(" ", "_") + "_" + todayDate + ".pdf";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

                Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);
                if (uri != null) {
                    try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                        pdfDocument.writeTo(outputStream);
                        Toast.makeText(this, "PDF saved to Documents folder.", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                java.io.File legacyFile = new java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName);
                pdfDocument.writeTo(new java.io.FileOutputStream(legacyFile));
                Toast.makeText(this, "PDF saved to Documents folder.", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e("PDF_GENERATION", "Error writing PDF: " + e.toString());
            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            pdfDocument.close();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                generatePdf();
            } else {
                Toast.makeText(this, "Permission Denied. Cannot save PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


// --- ADDED: A new class to convert numbers to words ---
class NumberToWordsConverter {

    private static final String[] units = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] tens = {
            "",        // 0
            "",        // 1
            "Twenty",  // 2
            "Thirty",  // 3
            "Forty",   // 4
            "Fifty",   // 5
            "Sixty",   // 6
            "Seventy", // 7
            "Eighty",  // 8
            "Ninety"   // 9
    };

    private static String convertLessThanOneThousand(int number) {
        String current;

        if (number % 100 < 20) {
            current = units[number % 100];
            number /= 100;
        } else {
            current = units[number % 10];
            number /= 10;
            current = tens[number % 10] + (current.isEmpty() ? "" : " " + current);
            number /= 10;
        }
        if (number == 0) return current;
        return units[number] + " Hundred" + (current.isEmpty() ? "" : " " + current);
    }

    public static String convert(long number) {
        if (number == 0) {
            return "Zero";
        }

        String current = "";
        long place = 0;

        do {
            long n = number % 1000;
            if (n != 0) {
                String s = convertLessThanOneThousand((int)n);
                String placeValue = "";
                switch ((int)place) {
                    case 1: placeValue = " Thousand"; break;
                    case 2: placeValue = " Lakh"; break;
                    case 3: placeValue = " Crore"; break;
                }
                current = s + placeValue + (current.isEmpty() ? "" : " " + current);
            }
            place++;
            number /= 1000;
        } while (number > 0);

        return current.trim();
    }
}