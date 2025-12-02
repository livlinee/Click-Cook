package com.example.clickncook.controllers.admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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

        getSupportFragmentManager().beginTransaction().replace(R.id.admin_container, new AdminHomeFragment()).commit();

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_dashboard) {
                getSupportFragmentManager().beginTransaction().replace(R.id.admin_container, new AdminHomeFragment()).commit();
                return true;
            } else if (id == R.id.menu_users) {
                getSupportFragmentManager().beginTransaction().replace(R.id.admin_container, new AdminUsersFragment()).commit();
                return true;
            } else if (id == R.id.menu_reports) {
                getSupportFragmentManager().beginTransaction().replace(R.id.admin_container, new AdminReportsFragment()).commit();
                return true;
            }
            // Tambahkan item menu ini di XML menu/admin_nav_menu.xml jika ingin navigasi Content
            // else if (id == R.id.menu_content) {
            //    getSupportFragmentManager().beginTransaction().replace(R.id.admin_container, new AdminContentFragment()).commit();
            //    return true;
            // }
            return false;
        });
    }
}