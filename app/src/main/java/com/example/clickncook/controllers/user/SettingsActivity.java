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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsActivity extends AppCompatActivity {

    private EditText etName, etBio;
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
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        imgProfile = findViewById(R.id.img_profile_edit);
        etName = findViewById(R.id.et_edit_name);
        etBio = findViewById(R.id.et_edit_bio);
        btnSave = findViewById(R.id.btn_save_profile);
        btnLogout = findViewById(R.id.btn_logout);

        loadUserData();

        ActivityResultLauncher<String> launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        imgProfile.setImageURI(uri);
                    }
                });
        imgProfile.setOnClickListener(v -> launcher.launch("image/*"));

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
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                etName.setText(doc.getString("name"));
                etBio.setText(doc.getString("bio"));
                currentPhotoUrl = doc.getString("photoUrl");
                if (currentPhotoUrl != null) Glide.with(this).load(currentPhotoUrl).circleCrop().into(imgProfile);
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
                                    Toast.makeText(this, "Gagal update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            });
                });
    }
}