package com.example.main.VisitorOperation;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.Manifest;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.main.FirebaseUtils;
import com.example.main.R;
import com.example.main.VisitorOperation.MyAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Bill2Fragment extends Fragment {

    Button createPDF;
    ArrayAdapter<String> adapter1;
    ArrayAdapter<String> adapter2;
    String Visname;
    boolean flag = false;

    DatabaseReference reference;

    private Spinner monthSp, yearSp;
    private String monthA[], yearA[];

    private String name, month, year, month_year;
    public static String TH_amount, PR_amount;

    int th_cont = 0, pr_cont = 0, th_remun = 0, pr_remun = 0, total_remun = 0, professional_tax = 0, total = 0;
    private TextView name1;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill2, container, false);
        Context context = getContext();

        name1 = view.findViewById(R.id.detailname);
        Visname = MyAdapter.visitorName;
        monthSp = view.findViewById(R.id.month);
        yearSp = view.findViewById(R.id.year);

        monthA = new String[]{
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, monthA);
        monthSp.setAdapter(adapter1);

        yearA = new String[]{"2022", "2023", "2024", "2025"};
        adapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, yearA);
        yearSp.setAdapter(adapter2);

        createPDF = view.findViewById(R.id.createPDF);

        // Request permissions
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        createPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = Visname;
                month = monthSp.getSelectedItem().toString();
                year = yearSp.getSelectedItem().toString();
                month_year = month + year;

                // Reset counts
                th_cont = 0;
                pr_cont = 0;
                flag = false;

                Log.d("BillDebug", "Starting process for: " + name + ", " + month + " " + year);

                // Get lecture counts first
                getLectureCounts(new LectureCountCallback() {
                    @Override
                    public void onCountReceived() {
                        Log.d("BillDebug", "Lecture counts received: TH=" + th_cont + ", PR=" + pr_cont);
                        // After getting counts, get salary information
                        getSalaryInfo(new SalaryCallback() {
                            @Override
                            public void onSalaryReceived() {
                                Log.d("BillDebug", "Salary info received");
                                // Now generate PDF
                                generatePDF();
                            }
                        });
                    }
                });
            }
        });

        return view;
    }

    private interface LectureCountCallback {
        void onCountReceived();
    }

    private interface SalaryCallback {
        void onSalaryReceived();
    }

    private void getLectureCounts(LectureCountCallback callback) {
        String targetType1 = "TH";
        String targetType2 = "PR";

        reference = FirebaseUtils.getDatabase().getReference("Visitors");

        reference.child(Visname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int localTHCount = 0;
                int localPRCount = 0;
                boolean hasLectures = false;

                Log.d("FirebaseDebug", "DataSnapshot exists: " + dataSnapshot.exists());

                if (!dataSnapshot.exists()) {
                    flag = true;
                    callback.onCountReceived();
                    return;
                }

                for (DataSnapshot calendarSnapshot : dataSnapshot.getChildren()) {
                    String calendarKey = calendarSnapshot.getKey();
                    Log.d("FirebaseDebug", "Calendar key: " + calendarKey);

                    for (DataSnapshot dateSnapshot : calendarSnapshot.getChildren()) {
                        String date = dateSnapshot.getKey();
                        Log.d("FirebaseDebug", "Date key: " + date);

                        try {
                            // Handle different date formats - try both DD-MM-YYYY and MM-DD-YYYY
                            String[] dateParts = date.split("-");
                            if (dateParts.length != 3) {
                                continue;
                            }

                            // Try DD-MM-YYYY format first
                            String day = dateParts[0];
                            String monthPart = dateParts[1];
                            String yearPart = dateParts[2];

                            // If the day part is more than 31, it might be YYYY-MM-DD format
                            if (day.length() == 4) {
                                // It's YYYY-MM-DD format
                                yearPart = day;
                                monthPart = dateParts[1];
                                day = dateParts[2];
                            }

                            int monthNumber;
                            try {
                                monthNumber = Integer.parseInt(monthPart);
                            } catch (NumberFormatException e) {
                                continue;
                            }

                            String monthName = show(monthNumber);

                            Log.d("DateDebug", "Parsed: " + day + "-" + monthNumber + "-" + yearPart +
                                    " -> " + monthName + " comparing with " + month + " " + year);

                            if (monthName != null && monthName.equals(month) && yearPart.equals(year)) {
                                hasLectures = true;
                                Log.d("DateDebug", "MATCH FOUND for " + month + " " + year);

                                for (DataSnapshot timeSnapshot : dateSnapshot.getChildren()) {
                                    String type = timeSnapshot.child("calender_t").getValue(String.class);
                                    Log.d("LectureDebug", "Time entry type: " + type);

                                    if (type != null) {
                                        if (type.equals(targetType1)) {
                                            localTHCount++;
                                            Log.d("CountDebug", "TH count: " + localTHCount);
                                        } else if (type.equals(targetType2)) {
                                            localPRCount++;
                                            Log.d("CountDebug", "PR count: " + localPRCount);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e("DateParseError", "Error parsing date: " + date, e);
                            continue;
                        }
                    }
                }

                th_cont = localTHCount;
                pr_cont = localPRCount;
                flag = !hasLectures;

                Log.d("FinalCount", "TH: " + th_cont + ", PR: " + pr_cont + ", Has lectures: " + hasLectures);

                callback.onCountReceived();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error getting data: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Error getting lecture counts", Toast.LENGTH_SHORT).show();
                callback.onCountReceived();
            }
        });
    }

    private void getSalaryInfo(SalaryCallback callback) {
        reference = FirebaseUtils.getDatabase().getReference("Salary");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    TH_amount = snapshot.child("salary_th").getValue(String.class);
                    PR_amount = snapshot.child("salary_pr").getValue(String.class);

                    Log.d("SalaryDebug", "TH_amount: " + TH_amount + ", PR_amount: " + PR_amount);

                    if (TH_amount != null && PR_amount != null) {
                        try {
                            th_remun = Integer.parseInt(TH_amount);
                            pr_remun = Integer.parseInt(PR_amount);
                        } catch (NumberFormatException e) {
                            Log.e("SalaryError", "Error parsing salary values", e);
                            th_remun = 0;
                            pr_remun = 0;
                        }
                    } else {
                        th_remun = 0;
                        pr_remun = 0;
                    }
                } else {
                    th_remun = 0;
                    pr_remun = 0;
                }

                total_remun = (th_cont * th_remun) + (pr_cont * pr_remun);
                professional_tax = 0;
                total = total_remun + professional_tax;

                Log.d("SalaryDebug", "Final: th_remun=" + th_remun + ", pr_remun=" + pr_remun + ", total=" + total);

                callback.onSalaryReceived();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SalaryError", "Error getting salary: " + error.getMessage());
                Toast.makeText(getContext(), "Error getting salary info", Toast.LENGTH_SHORT).show();
                callback.onSalaryReceived();
            }
        });
    }

    private void generatePDF() {
        if (flag) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("VisitorFunds");
            builder.setIcon(R.drawable.baseline_warning_24);
            builder.setMessage("There is no lecture or practical taken in this month or year..");
            builder.setCancelable(false);
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create().show();
            return;
        }

        try {
            PdfDocument myPdfDocument = new PdfDocument();
            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size in points
            PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);

            Paint paint = new Paint();
            Paint linePaint = new Paint();
            Canvas canvas = myPage.getCanvas();

            // Set background color
            canvas.drawColor(Color.WHITE);

            // Set text properties
            paint.setColor(Color.BLACK);
            paint.setTextSize(20);

            // Draw header
            canvas.drawText("Shri Guru Gobind Singhji Institute of Engineering and Technology", 50, 50, paint);
            paint.setTextSize(14);
            canvas.drawText("(An autonomous institute of Govt. of Maharashtra)", 120, 70, paint);

            // Draw line
            linePaint.setStrokeWidth(2);
            linePaint.setColor(Color.BLACK);
            canvas.drawLine(50, 80, 545, 80, linePaint);

            // Draw title
            paint.setTextSize(18);
            paint.setFakeBoldText(true);
            canvas.drawText("Visiting Faculty Bill", 200, 100, paint);
            paint.setFakeBoldText(false);

            paint.setTextSize(14);
            canvas.drawText("Year: " + year, 250, 120, paint);

            // Draw program info
            canvas.drawText("PROGRAMME: Information Technology", 50, 140, paint);

            // Draw table
            linePaint.setStrokeWidth(1);

            // Table header
            paint.setTextSize(12);
            paint.setFakeBoldText(true);

            // Draw horizontal lines
            float[] tableLines = {80, 160, 240, 320, 400, 480, 560};
            for (float y : tableLines) {
                canvas.drawLine(50, y, 545, y, linePaint);
            }

            // Draw vertical lines
            float[] verticalLines = {50, 120, 200, 280, 360, 440, 520, 545};
            for (float x : verticalLines) {
                canvas.drawLine(x, 160, x, 480, linePaint);
            }

            // Table headers
            canvas.drawText("Sn.", 60, 175, paint);
            canvas.drawText("Visiting Faculty name", 125, 175, paint);
            canvas.drawText("Month", 205, 175, paint);
            canvas.drawText("TH Conducted", 285, 175, paint);
            canvas.drawText("PR Conducted", 365, 175, paint);
            canvas.drawText("TH Remuneration", 445, 175, paint);
            canvas.drawText("PR Remuneration", 525, 175, paint);

            paint.setFakeBoldText(false);

            // Table data
            canvas.drawText("1", 60, 200, paint);
            canvas.drawText(name, 125, 200, paint);
            canvas.drawText(month_year, 205, 200, paint);
            canvas.drawText(String.valueOf(th_cont), 285, 200, paint);
            canvas.drawText(String.valueOf(pr_cont), 365, 200, paint);
            canvas.drawText(String.valueOf(th_remun), 445, 200, paint);
            canvas.drawText(String.valueOf(pr_remun), 525, 200, paint);

            // Total section
            paint.setFakeBoldText(true);
            canvas.drawText("Total Remuneration: " + total_remun, 50, 250, paint);
            canvas.drawText("Professional Tax: " + professional_tax, 50, 270, paint);
            canvas.drawText("Grand Total: " + total, 50, 290, paint);
            paint.setFakeBoldText(false);

            // Certification text
            paint.setTextSize(10);
            canvas.drawText("It is certified that the above visiting faculty working in the department of Information Technology", 50, 320, paint);
            canvas.drawText("have conducted Theory and Practical workload as given above for the month of " + month_year, 50, 335, paint);
            canvas.drawText("It is recommended to pass the honorarium Amount of RS " + total, 50, 350, paint);

            // Signatures
            paint.setTextSize(12);
            canvas.drawText("Head of the Department", 50, 400, paint);
            canvas.drawText("_________________________", 50, 420, paint);

            canvas.drawText("Verified, the honorarium Amount of Rs." + total + " is approved", 50, 450, paint);
            canvas.drawText("and shall be passed under PP && SS grant for financial year " + year, 50, 465, paint);

            canvas.drawText("Registrar/AO", 350, 400, paint);
            canvas.drawText("_________________________", 350, 420, paint);

            canvas.drawText("Principal", 350, 450, paint);
            canvas.drawText("_________________________", 350, 470, paint);

            myPdfDocument.finishPage(myPage);

            // Save the document
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "PDFs");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = new File(directory, name + "_" + month_year + ".pdf").getAbsolutePath();
            File file = new File(filePath);

            try {
                FileOutputStream fos = new FileOutputStream(file);
                myPdfDocument.writeTo(fos);
                fos.close();
                Toast.makeText(getContext(), "PDF created successfully: " + filePath, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error creating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            myPdfDocument.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error generating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String show(int month) {
        String monthName;
        switch (month) {
            case 1: monthName = "January"; break;
            case 2: monthName = "February"; break;
            case 3: monthName = "March"; break;
            case 4: monthName = "April"; break;
            case 5: monthName = "May"; break;
            case 6: monthName = "June"; break;
            case 7: monthName = "July"; break;
            case 8: monthName = "August"; break;
            case 9: monthName = "September"; break;
            case 10: monthName = "October"; break;
            case 11: monthName = "November"; break;
            case 12: monthName = "December"; break;
            default: monthName = null; break;
        }
        return monthName;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}