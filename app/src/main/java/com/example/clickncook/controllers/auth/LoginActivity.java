package com.example.clickncook.controllers.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clickncook.R;
import com.example.clickncook.controllers.admin.AdminMainActivity;
import com.example.clickncook.controllers.user.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register_link);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        progressBar = findViewById(R.id.progress_bar);

        btnLogin.setOnClickListener(v -> performLogin());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email wajib diisi");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password wajib diisi");
            return;
        }

        setLoading(true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkUserRoleAndStatus(mAuth.getCurrentUser().getUid());
                    } else {
                        setLoading(false);
                        Toast.makeText(LoginActivity.this, "Login Gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkUserRoleAndStatus(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    setLoading(false);

                    if (document.exists()) {
                        Boolean isBlocked = document.getBoolean("isBlocked");
                        if (isBlocked != null && isBlocked) {
                            mAuth.signOut();
                            Toast.makeText(this, "Akun Anda telah DIBLOKIR oleh Admin karena pelanggaran.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        String role = document.getString("role");
                        if ("admin".equals(role)) {
                            Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Hapus history back stack
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        finish();

                    } else {
                        Toast.makeText(this, "Data pengguna tidak ditemukan di Database.", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    mAuth.signOut();
                    Toast.makeText(this, "Gagal mengambil data profil. Cek koneksi internet.", Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnLogin.setText("Memuat...");
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");
        }
    }
}