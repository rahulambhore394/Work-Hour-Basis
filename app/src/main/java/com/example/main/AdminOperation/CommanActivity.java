package com.example.main.AdminOperation;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.main.ActivityLogSheetActivity;
import com.example.main.ConveyanceLogActivity;
import com.example.main.EventOperation.VisitorEventActivity;
import com.example.main.GuestFacultyDetails;
import com.example.main.R;
import com.example.main.RemunerationBillActivity;
import com.example.main.UploadPDF_Operation.UploadPDFActivity;
import com.example.main.VisitorOperation.DetailActivity;
import com.example.main.VisitorOperation.VisitorActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// It's good practice to implement the listener interface
public class CommanActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Use constants for Firebase keys to avoid typos
    private static final String FIREBASE_SALARY_NODE = "Salary";
    private static final String FIREBASE_THEORY_KEY = "theory";
    private static final String FIREBASE_PRACTICAL_KEY = "practical";
    private static final String TAG = "CommanActivity";

    private DrawerLayout drawerLayout;
    private ImageSlider imageSlider;
    private TextView tx1, tx2;
    private DatabaseReference reference;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comman);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        imageSlider = findViewById(R.id.imageSlider);
        tx1 = findViewById(R.id.th);
        tx2 = findViewById(R.id.pr);

        setupImageSlider();
        fetchSalaryData();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupImageSlider() {
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.welcome, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.welcome2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image3, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
    }

    private void fetchSalaryData() {
        reference = FirebaseDatabase.getInstance(getString(R.string.database_instance)).getReference(FIREBASE_SALARY_NODE);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // FIX: Added null checks to prevent crashes if data doesn't exist in Firebase.
                if (snapshot.exists()) {
                    String thAmount = snapshot.child(FIREBASE_THEORY_KEY).getValue(String.class);
                    String prAmount = snapshot.child(FIREBASE_PRACTICAL_KEY).getValue(String.class);

                    // FIX: Use Log.d for debugging instead of System.out.println
                    Log.d(TAG, "Theory Amount: " + thAmount + ", Practical Amount: " + prAmount);

                    tx1.setText("Theory's\n Amount: " + (thAmount != null ? thAmount : "N/A"));
                    tx2.setText("Practical's Amount: " + (prAmount != null ? prAmount : "N/A"));
                } else {
                    Log.w(TAG, "Salary node does not exist in the database.");
                    tx1.setText("Theory's\n Amount: N/A");
                    tx2.setText("Practical's Amount: N/A");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // FIX: Log the error to help with debugging database issues.
                Log.e(TAG, "Firebase database error: " + error.getMessage());
                Toast.makeText(CommanActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // FIX: Using if-else if is the recommended modern approach for menu items.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Intent i = null;

        if (itemId == R.id.nav_salary) {
            i = new Intent(this, SalaryActivity.class);
        } else if (itemId == R.id.nav_upload) {
            i = new Intent(this, UploadPDFActivity.class);
        } else if (itemId == R.id.nav_show_visitor) {
            i = new Intent(this, DetailActivity.class);
        } else if (itemId == R.id.nav_add_visitor) {
            i = new Intent(this, VisitorActivity.class);
        } else if (itemId == R.id.nav_event) {
            i = new Intent(this, VisitorEventActivity.class);
        } else if (itemId == R.id.visiting_faculty) {
            i = new Intent(this, GuestFacultyDetails.class);
        }    else if (itemId == R.id.RemunerationBillActivity) {
            i = new Intent(this, RemunerationBillActivity.class);
        } else if (itemId == R.id.LogSheet) {
            i = new Intent(this, ActivityLogSheetActivity.class);
        }else if (itemId == R.id.Conveyance) {
            i = new Intent(this, ConveyanceLogActivity.class);
        }

        if (i != null) {
            startActivity(i);
        }

        // FIX: The drawer should always be closed after an item is selected.
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.right_corner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.right_item1) {
            // FIX: Added .show() to actually display the Toast.
            Toast.makeText(this, "About Admin selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, About_Admin_Activity.class));
            return true;
        } else if (itemId == R.id.right_item4) {
            Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        } else if (itemId == R.id.right_item2) {
            Toast.makeText(this, "About App selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (itemId == R.id.right_item3) {
            showLogoutDialog();
            return true; // FIX: Return true to indicate the event was handled.
        }

        // If no item was handled, pass it to the superclass.
        return super.onOptionsItemSelected(item);
    }

    // IMPROVEMENT: Extracted the duplicated logout dialog logic into its own method.
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setIcon(R.drawable.baseline_exit_to_app_24)
                .setMessage("Are you sure you want to logout?")
                .setCancelable(true) // Allow user to dismiss by tapping outside
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Yes", (dialog, which) -> {
                    Toast.makeText(CommanActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    // FIX: Clear the activity stack so the user cannot press back to return here after logout.
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        // FIX: The correct behavior for onBackPressed with a navigation drawer.
        // If the drawer is open, close it. Otherwise, perform the default back action.
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}