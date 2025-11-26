package com.example.main.VisitorOperation;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.main.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Detail4Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail4);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 1. Set the initial fragment when the activity is first created.
        // This prevents a blank screen before the user makes a selection.
        if (savedInstanceState == null) {
            replaceFragment(new Home2Fragment());
        }

        // 2. FIX: Use setOnItemSelectedListener, as the old one is deprecated.
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // 3. IMPROVEMENT: Use if-else if, which is the recommended modern approach.
            if (itemId == R.id.home) {
                replaceFragment(new Home2Fragment());
                return true;
            } else if (itemId == R.id.bill) {
                replaceFragment(new Bill2Fragment());
                return true;
            } else if (itemId == R.id.info) {
                replaceFragment(new Info2Fragment());
                return true;
            }

            return false;
        });
    }

    /**
     * Helper method to replace the current fragment in the container.
     * @param fragment The new fragment to display.
     */
    // 4. IMPROVEMENT: Extracted fragment transaction logic into a helper method
    // to avoid repeating code (DRY principle).
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.bottom_container, fragment)
                .commit();
    }
}