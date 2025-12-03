package com.example.clickncook.controllers.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.clickncook.R;
import com.example.clickncook.models.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddRecipeActivity extends AppCompatActivity {

    private ImageView imgPreview;
    private EditText etTitle;
    private LinearLayout layoutIngredients, layoutSteps;
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

        layoutIngredients = findViewById(R.id.layoutIngredientsContainer);
        layoutSteps = findViewById(R.id.layoutStepsContainer);

        spCategory = findViewById(R.id.spCategory);
        spTime = findViewById(R.id.spTime);
        spDifficulty = findViewById(R.id.spDifficulty);

        Button btnPublish = findViewById(R.id.btnPublish);
        Button btnDraft = findViewById(R.id.btnDraft);
        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);
        Button btnAddStep = findViewById(R.id.btnAddStep);

        setupSpinners();

        ActivityResultLauncher<String> launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        imgPreview.setImageURI(uri);
                        imgPreview.setPadding(0,0,0,0);
                        imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        findViewById(R.id.tvUploadHint).setVisibility(View.GONE);
                    }
                });

        findViewById(R.id.layoutUploadPhoto).setOnClickListener(v -> launcher.launch("image/*"));

        btnAddIngredient.setOnClickListener(v -> addIngredientField(""));
        btnAddStep.setOnClickListener(v -> addStepField(""));

        btnPublish.setOnClickListener(v -> uploadImageAndSave(false));
        btnDraft.setOnClickListener(v -> uploadImageAndSave(true));
    }

    private void setupSpinners() {
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, R.layout.item_spinner,
                new String[]{"Nusantara", "Western", "Dessert", "Minuman", "Healthy"});
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(catAdapter);

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, R.layout.item_spinner,
                new String[]{"< 15 min", "15-30 min", "30-60 min", "> 1 jam"});
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTime.setAdapter(timeAdapter);

        ArrayAdapter<String> diffAdapter = new ArrayAdapter<>(this, R.layout.item_spinner,
                new String[]{"Mudah", "Sedang", "Sulit"});
        diffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDifficulty.setAdapter(diffAdapter);
    }

    private void addIngredientField(String text) {
        EditText et = new EditText(this);
        et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        et.setHint("Bahan selanjutnya...");
        et.setText(text);
        et.setTextColor(ContextCompat.getColor(this, R.color.black_text));
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        et.setBackgroundResource(R.drawable.bg_input_field);
        et.setPadding(32, 32, 32, 32);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) et.getLayoutParams();
        params.setMargins(0, 16, 0, 0);
        et.setLayoutParams(params);
        layoutIngredients.addView(et);
    }

    private void addStepField(String text) {
        EditText et = new EditText(this);
        et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        et.setHint("Langkah selanjutnya...");
        et.setText(text);
        et.setTextColor(ContextCompat.getColor(this, R.color.black_text));
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        et.setBackgroundResource(R.drawable.bg_input_field);
        et.setPadding(32, 32, 32, 32);
        et.setMinLines(2);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) et.getLayoutParams();
        params.setMargins(0, 16, 0, 0);
        et.setLayoutParams(params);
        layoutSteps.addView(et);
    }

    private void uploadImageAndSave(boolean isDraft) {
        if (imageUri == null && !isDraft) {
            Toast.makeText(this, "Wajib pilih foto untuk penerbitan!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            String filename = UUID.randomUUID().toString();
            StorageReference ref = storageRef.child("recipe_images/" + filename);
            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveToFirestore(uri.toString(), isDraft);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal upload foto", Toast.LENGTH_SHORT).show());
        } else {
            saveToFirestore(null, isDraft);
        }
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
        recipe.setIsDraft(isDraft);

        List<String> ingredients = new ArrayList<>();
        for(int i=0; i<layoutIngredients.getChildCount(); i++) {
            View v = layoutIngredients.getChildAt(i);
            if(v instanceof EditText) {
                String val = ((EditText)v).getText().toString().trim();
                if(!val.isEmpty()) ingredients.add(val);
            }
        }
        recipe.setIngredients(ingredients);

        List<String> steps = new ArrayList<>();
        for(int i=0; i<layoutSteps.getChildCount(); i++) {
            View v = layoutSteps.getChildAt(i);
            if(v instanceof EditText) {
                String val = ((EditText)v).getText().toString().trim();
                if(!val.isEmpty()) steps.add(val);
            }
        }
        recipe.setSteps(steps);

        recipe.setAverageRating(0.0);
        recipe.setTotalReviews(0);
        recipe.setViewCount(0);

        db.collection("recipes").add(recipe)
                .addOnSuccessListener(doc -> {
                    db.collection("users").document(user.getUid())
                            .update("totalRecipes", FieldValue.increment(1));
                    Toast.makeText(this, isDraft ? "Disimpan ke Draf" : "Resep Terbit!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Gagal menyimpan resep: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(boolean isLoading) {
        Button btnPublish = findViewById(R.id.btnPublish);
        Button btnDraft = findViewById(R.id.btnDraft);
        btnPublish.setEnabled(!isLoading);
        btnDraft.setEnabled(!isLoading);
        btnPublish.setText(isLoading ? "Menyimpan..." : "Terbitkan");
    }
}