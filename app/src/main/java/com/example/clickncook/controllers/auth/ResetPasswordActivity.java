package com.example.clickncook.controllers.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clickncook.R;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etPass, etConfirm;
    private Button btnReset;
    private String oobCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etPass = findViewById(R.id.etPass);
        etConfirm = findViewById(R.id.etConfirm);
        btnReset = findViewById(R.id.btnReset);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            oobCode = intent.getData().getQueryParameter("oobCode");
        }

        btnReset.setEnabled(true);
        btnReset.setOnClickListener(v -> performReset());
    }

    private void performReset() {
        String pass = etPass.getText().toString();
        String confirm = etConfirm.getText().toString();

        if (TextUtils.isEmpty(pass) || pass.length() < 6) {
            etPass.setError("Password minimal 6 karakter");
            return;
        }

        if (!pass.equals(confirm)) {
            etConfirm.setError("Password tidak sama");
            return;
        }

        if (oobCode != null) {
            FirebaseAuth.getInstance().confirmPasswordReset(oobCode, pass)
                    .addOnSuccessListener(aVoid -> showSuccessDialog())
                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal reset: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Mode Demo: Password tervalidasi (Link tidak ditemukan)", Toast.LENGTH_SHORT).show();
            showSuccessDialog();
        }
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_reset_success, null);
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnLoginDialog).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}