package com.example.clickncook.controllers.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clickncook.R;
import com.example.clickncook.controllers.user.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView tvTimer;
    private Button btnResend;
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        TextView tvEmail = findViewById(R.id.tvUserEmail);
        tvTimer = findViewById(R.id.tvTimer);
        btnResend = findViewById(R.id.btnResend);

        if (user != null) {
            tvEmail.setText(user.getEmail());
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnResend.setOnClickListener(v -> {
            if (user != null && !isTimerRunning) {
                user.sendEmailVerification()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Email verifikasi dikirim ulang", Toast.LENGTH_SHORT).show();
                            startTimer();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Gagal mengirim ulang", Toast.LENGTH_SHORT).show());
            }
        });

        startTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.reload().addOnSuccessListener(aVoid -> {
                if (user.isEmailVerified()) {
                    Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void startTimer() {
        isTimerRunning = true;
        btnResend.setEnabled(false);
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Kirim ulang dalam 00:" + String.format("%02d", millisUntilFinished / 1000));
            }

            public void onFinish() {
                tvTimer.setText("Kirim ulang tersedia");
                btnResend.setEnabled(true);
                isTimerRunning = false;
            }
        }.start();
    }
}