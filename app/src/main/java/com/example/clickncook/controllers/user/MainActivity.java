package com.example.clickncook.controllers.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.clickncook.R;
import com.example.clickncook.controllers.auth.LoginActivity;
import com.example.clickncook.controllers.user.fragments.FavoriteFragment;
import com.example.clickncook.controllers.user.fragments.HomeFragment;
import com.example.clickncook.controllers.user.fragments.ProfileFragment;
import com.example.clickncook.controllers.user.fragments.ReviewHistoryFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        LinearLayout navHome = findViewById(R.id.navRecipe);
        LinearLayout navFavorite = findViewById(R.id.navFavorite);
        LinearLayout navReview = findViewById(R.id.navReview);
        LinearLayout navProfile = findViewById(R.id.navProfile);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        loadFragment(new HomeFragment());

        navHome.setOnClickListener(v -> loadFragment(new HomeFragment()));

        navFavorite.setOnClickListener(v -> {
            if (!checkGuest()) loadFragment(new FavoriteFragment());
        });

        navReview.setOnClickListener(v -> {
            if (!checkGuest()) loadFragment(new ReviewHistoryFragment());
        });

        navProfile.setOnClickListener(v -> {
            if (!checkGuest()) loadFragment(new ProfileFragment());
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