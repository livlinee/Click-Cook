package com.example.clickncook.controllers.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.clickncook.R;
import com.example.clickncook.controllers.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsActivity extends AppCompatActivity {

    private EditText etName, etBio, etOldPass, etNewPass;
    private ImageView imgProfile;
    private Button btnSave, btnLogout;
    private Uri imageUri;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private String currentPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_settings);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        imgProfile = findViewById(R.id.imgProfilePhoto);
        etName = findViewById(R.id.etName);
        etBio = findViewById(R.id.etBio);
        etOldPass = findViewById(R.id.etOldPass);
        etNewPass = findViewById(R.id.etNewPass);
        btnSave = findViewById(R.id.btnSavePassword);
        btnLogout = findViewById(R.id.btnLogout);
        ImageView btnChangePhoto = findViewById(R.id.btnChangePhoto);

        loadUserData();

        ActivityResultLauncher<String> launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        imgProfile.setImageURI(uri);
                    }
                });

        btnChangePhoto.setOnClickListener(v -> launcher.launch("image/*"));

        btnSave.setOnClickListener(v -> saveProfile());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserData() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                etName.setText(doc.getString("name"));
                etBio.setText(doc.getString("bio"));
                currentPhotoUrl = doc.getString("photoUrl");
                if (currentPhotoUrl != null) Glide.with(this).load(currentPhotoUrl).centerCrop().into(imgProfile);
            }
        });
    }

    private void saveProfile() {
        String newName = etName.getText().toString();
        String newBio = etBio.getText().toString();

        if (TextUtils.isEmpty(newName)) {
            etName.setError("Nama tidak boleh kosong");
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Menyimpan...");

        if (imageUri != null) {
            StorageReference ref = storageRef.child("profile_images/" + mAuth.getCurrentUser().getUid());
            ref.putFile(imageUri).addOnSuccessListener(task -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                performBatchUpdate(newName, newBio, uri.toString());
            }));
        } else {
            performBatchUpdate(newName, newBio, currentPhotoUrl);
        }
    }

    private void performBatchUpdate(String name, String bio, String photoUrl) {
        String uid = mAuth.getCurrentUser().getUid();
        WriteBatch batch = db.batch();

        DocumentReference userRef = db.collection("users").document(uid);
        batch.update(userRef, "name", name, "bio", bio, "photoUrl", photoUrl);

        // Update juga data user di resep dan review agar sinkron
        db.collection("recipes").whereEqualTo("userId", uid).get()
                .addOnSuccessListener(recipeSnapshots -> {
                    for (DocumentSnapshot doc : recipeSnapshots) {
                        batch.update(doc.getReference(), "userName", name, "userPhotoUrl", photoUrl);
                    }
                    db.collection("reviews").whereEqualTo("userId", uid).get()
                            .addOnSuccessListener(reviewSnapshots -> {
                                for (DocumentSnapshot doc : reviewSnapshots) {
                                    batch.update(doc.getReference(), "userName", name, "userPhotoUrl", photoUrl);
                                }
                                batch.commit().addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Profil Berhasil Diupdate!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }).addOnFailureListener(e -> {
                                    btnSave.setEnabled(true);
                                    btnSave.setText("Simpan");
                                    Toast.makeText(this, "Gagal update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            });
                });
    }
}