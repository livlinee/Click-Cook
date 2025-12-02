package com.example.clickncook.controllers.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clickncook.R;
import com.example.clickncook.controllers.admin.AdminMainActivity;
import com.example.clickncook.controllers.user.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                        .get().addOnSuccessListener(document -> {
                            if (document.exists()) {
                                String role = document.getString("role");
                                if ("admin".equals(role)) {
                                    startActivity(new Intent(this, AdminMainActivity.class));
                                } else {
                                    startActivity(new Intent(this, MainActivity.class));
                                }
                            } else {
                                startActivity(new Intent(this, LoginActivity.class));
                            }
                            finish();
                        });
            } else {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }, 2000);
    }
}