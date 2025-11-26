package com.example.client;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class VisInfoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vis_info);

        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        //setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_about:
                Intent i = new Intent(getApplicationContext(),VisAboutActivity.class);
                Toast.makeText(VisInfoActivity.this, "About Us page", Toast.LENGTH_SHORT).show();
                startActivity(i);
                break;



            case R.id.nav_logut:
                AlertDialog.Builder builder = new AlertDialog.Builder(VisInfoActivity.this);
                builder.setMessage("are you sure ?");
                builder.setCancelable(false);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }

                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),VisLoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(VisInfoActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
                break;



            default:
                return true;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}