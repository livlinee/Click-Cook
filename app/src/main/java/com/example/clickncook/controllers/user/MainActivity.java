package com.example.clickncook.controllers.user;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

    private LinearLayout navHome, navFavorite, navReview, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        navHome = findViewById(R.id.navRecipe);
        navFavorite = findViewById(R.id.navFavorite);
        navReview = findViewById(R.id.navReview);
        navProfile = findViewById(R.id.navProfile);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        loadFragment(new HomeFragment());
        updateNavUI(navHome);

        navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            updateNavUI(navHome); // Ubah warna jadi aktif
        });

        navFavorite.setOnClickListener(v -> {
            if (!checkGuest()) {
                loadFragment(new FavoriteFragment());
                updateNavUI(navFavorite); // Ubah warna jadi aktif
            }
        });

        navReview.setOnClickListener(v -> {
            if (!checkGuest()) {
                loadFragment(new ReviewHistoryFragment());
                updateNavUI(navReview); // Ubah warna jadi aktif
            }
        });

        navProfile.setOnClickListener(v -> {
            if (!checkGuest()) {
                loadFragment(new ProfileFragment());
                updateNavUI(navProfile);
            }
        });

        fabAdd.setOnClickListener(v -> {
            if (!checkGuest()) {
                startActivity(new Intent(this, AddRecipeActivity.class));
            }
        });
    }

    private void updateNavUI(LinearLayout activeLayout) {
        // Daftar semua menu navigasi
        LinearLayout[] menus = {navHome, navFavorite, navReview, navProfile};

        int colorActive = ContextCompat.getColor(this, R.color.primary_orange);
        int colorInactive = Color.parseColor("#BDBDBD");

        for (LinearLayout menu : menus) {
            ImageView icon = (ImageView) menu.getChildAt(0);
            TextView text = (TextView) menu.getChildAt(1);

            if (menu == activeLayout) {
                icon.setColorFilter(colorActive, PorterDuff.Mode.SRC_IN);
                text.setTextColor(colorActive);
            } else {
                icon.setColorFilter(colorInactive, PorterDuff.Mode.SRC_IN);
                text.setTextColor(colorInactive);
            }
        }
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