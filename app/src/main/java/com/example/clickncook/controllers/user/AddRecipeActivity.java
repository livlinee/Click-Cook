package com.example.clickncook.controllers.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clickncook.R;
import com.example.clickncook.models.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Arrays;
import java.util.UUID;

public class AddRecipeActivity extends AppCompatActivity {

    private ImageView imgPreview;
    private EditText etTitle, etIngredients, etSteps;
    private Spinner spCategory, spTime, spDifficulty;
    private Uri imageUri;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        imgPreview = findViewById(R.id.icCamera);
        etTitle = findViewById(R.id.etTitle);
        etIngredients = findViewById(R.id.etIngredient);
        etSteps = findViewById(R.id.etStep);
        spCategory = findViewById(R.id.spCategory);
        spTime = findViewById(R.id.spTime);
        spDifficulty = findViewById(R.id.spDifficulty);
        Button btnPublish = findViewById(R.id.btnPublish);
        Button btnDraft = findViewById(R.id.btnDraft);

        ActivityResultLauncher<String> launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        imgPreview.setImageURI(uri);
                        imgPreview.setPadding(0,0,0,0);
                        imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                });
        imgPreview.setOnClickListener(v -> launcher.launch("image/*"));

        btnPublish.setOnClickListener(v -> uploadImageAndSave(false));

        btnDraft.setOnClickListener(v -> uploadImageAndSave(true));
    }

    private void uploadImageAndSave(boolean isDraft) {
        if (imageUri == null) {
            Toast.makeText(this, "Wajib pilih foto!", Toast.LENGTH_SHORT).show();
            return;
        }

        String filename = UUID.randomUUID().toString();
        StorageReference ref = storageRef.child("recipe_images/" + filename);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveToFirestore(uri.toString(), isDraft);
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal upload foto", Toast.LENGTH_SHORT).show());
    }

    private void saveToFirestore(String imageUrl, boolean isDraft) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Recipe recipe = new Recipe();

        recipe.setTitle(etTitle.getText().toString());
        recipe.setCategory(spCategory.getSelectedItem().toString());
        recipe.setCookTime(spTime.getSelectedItem().toString());
        recipe.setDifficulty(spDifficulty.getSelectedItem().toString());
        recipe.setImageUrl(imageUrl);
        recipe.setUserId(user.getUid());
        recipe.setUserName(user.getDisplayName());
        recipe.setUserPhotoUrl(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
        recipe.setDraft(isDraft);

        String rawIngredients = etIngredients.getText().toString();
        recipe.setIngredients(Arrays.asList(rawIngredients.split("\\n")));

        String rawSteps = etSteps.getText().toString();
        recipe.setSteps(Arrays.asList(rawSteps.split("\\n")));

        db.collection("recipes").add(recipe)
                .addOnSuccessListener(doc -> {
                    db.collection("users").document(user.getUid())
                            .update("totalRecipes", FieldValue.increment(1));

                    Toast.makeText(this, isDraft ? "Disimpan ke Draf" : "Resep Terbit!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}