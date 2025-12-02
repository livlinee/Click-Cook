package com.example.clickncook.controllers.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.clickncook.R;
import com.example.clickncook.controllers.auth.LoginActivity;
import com.example.clickncook.controllers.user.fragments.FavoriteFragment;
import com.example.clickncook.controllers.user.fragments.HomeFragment;
import com.example.clickncook.controllers.user.fragments.ProfileFragment;
import com.example.clickncook.controllers.user.fragments.ReviewHistoryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        loadFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (id == R.id.nav_favorite) {
                if (checkGuest()) return false;
                loadFragment(new FavoriteFragment());
                return true;
            } else if (id == R.id.nav_reviews) {
                if (checkGuest()) return false;
                loadFragment(new ReviewHistoryFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                if (checkGuest()) return false;
                loadFragment(new ProfileFragment());
                return true;
            }
            return false;
        });

        fabAdd.setOnClickListener(v -> {
            if (!checkGuest()) {
                startActivity(new Intent(this, AddRecipeActivity.class));
            }
        });
    }

    private boolean checkGuest() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Silakan Login untuk mengakses fitur ini", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}