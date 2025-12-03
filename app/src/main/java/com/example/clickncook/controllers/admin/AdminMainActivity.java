package com.example.clickncook.controllers.admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.clickncook.R;
import com.example.clickncook.controllers.admin.fragments.AdminContentFragment;
import com.example.clickncook.controllers.admin.fragments.AdminHomeFragment;
import com.example.clickncook.controllers.admin.fragments.AdminReportsFragment;
import com.example.clickncook.controllers.admin.fragments.AdminUsersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        BottomNavigationView bottomNav = findViewById(R.id.admin_bottom_nav);

        loadFragment(new AdminHomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_dashboard) {
                loadFragment(new AdminHomeFragment());
                return true;
            } else if (id == R.id.menu_content) {
                // Perbaikan: Fitur ini sekarang aktif
                loadFragment(new AdminContentFragment());
                return true;
            } else if (id == R.id.menu_users) {
                loadFragment(new AdminUsersFragment());
                return true;
            } else if (id == R.id.menu_reports) {
                loadFragment(new AdminReportsFragment());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_container, fragment)
                .commit();
    }
}