package com.example.clickncook.controllers.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clickncook.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        EditText etEmail = findViewById(R.id.etEmail);
        Button btnSend = findViewById(R.id.btnSendLink);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSend.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSend.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email wajib diisi");
                return;
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Link reset password dikirim ke email", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}