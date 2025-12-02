package com.example.clickncook.controllers.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.clickncook.R;
import com.example.clickncook.controllers.auth.LoginActivity;
import com.example.clickncook.models.Bookmark;
import com.example.clickncook.models.Recipe;
import com.example.clickncook.models.Report;
import com.example.clickncook.models.Review;
import com.example.clickncook.views.adapter.IngredientAdapter;
import com.example.clickncook.views.adapter.ReviewAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class DetailRecipeActivity extends AppCompatActivity {

    private Recipe recipe;
    private ImageButton btnFavorite;
    private boolean isFavorited = false;
    private String bookmarkId = null;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView rvIngredients, rvSteps, rvReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recipe = (Recipe) getIntent().getSerializableExtra("RECIPE_DATA");

        ImageView imgCover = findViewById(R.id.recipe_image);
        TextView tvTitle = findViewById(R.id.recipe_title);
        TextView tvTime = findViewById(R.id.recipe_duration);
        RatingBar ratingBar = findViewById(R.id.rating_bar);
        btnFavorite = findViewById(R.id.btn_favorite_inline);
        ImageButton btnBack = findViewById(R.id.btn_back);
        ImageButton btnReport = findViewById(R.id.btn_warning_header);

        rvIngredients = findViewById(R.id.rvIngredients);
        rvSteps = findViewById(R.id.rvSteps);
        rvReviews = findViewById(R.id.rvReviews);

        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        rvSteps.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setLayoutManager(new LinearLayoutManager(this));

        if (recipe != null) {
            tvTitle.setText(recipe.getTitle());
            tvTime.setText(recipe.getCookTime());
            if (recipe.getAverageRating() > 0) {
                ratingBar.setRating((float) recipe.getAverageRating());
            }
            Glide.with(this).load(recipe.getImageUrl()).into(imgCover);

            if (mAuth.getCurrentUser() != null) checkFavoriteStatus();
        }

        findViewById(R.id.btn_bahan).setOnClickListener(v -> showTab("ingredients"));
        findViewById(R.id.btn_cara_masak).setOnClickListener(v -> showTab("steps"));
        findViewById(R.id.btn_lihat_ulasan).setOnClickListener(v -> showTab("reviews"));

        showIngredients();
        showSteps();
        showReviews();
        showTab("ingredients");

        btnFavorite.setOnClickListener(v -> toggleFavorite());
        btnBack.setOnClickListener(v -> finish());

        findViewById(R.id.btn_tulis_ulasan).setOnClickListener(v -> {
            if (checkGuest()) return;
            Intent intent = new Intent(this, WriteReviewActivity.class);
            intent.putExtra("RECIPE_ID", recipe.getId());
            intent.putExtra("RECIPE_TITLE", recipe.getTitle());
            intent.putExtra("RECIPE_IMG", recipe.getImageUrl());
            startActivity(intent);
        });

        btnReport.setOnClickListener(v -> {
            if (checkGuest()) return;
            showReportDialog();
        });
    }

    private void showReportDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_report_user, null);
        dialog.setContentView(view);

        RadioGroup rgReasons = view.findViewById(R.id.rgReportReasons);
        EditText etDetail = view.findViewById(R.id.etDetail);

        rgReasons.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbOther) {
                etDetail.setVisibility(View.VISIBLE);
            } else {
                etDetail.setVisibility(View.GONE);
            }
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        view.findViewById(R.id.btnSubmitReport).setOnClickListener(v -> {
            int selectedId = rgReasons.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Pilih alasan laporan", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRb = view.findViewById(selectedId);
            String reason = selectedRb.getText().toString();
            String detail = etDetail.getText().toString();

            Report report = new Report();
            report.setReporterUserId(mAuth.getCurrentUser().getUid());
            report.setReportedContentId(recipe.getId());
            report.setContentType("recipe");
            report.setReason(reason);
            report.setDetail(detail);
            report.setStatus("Pending");

            db.collection("reports").add(report).addOnSuccessListener(doc -> {
                Toast.makeText(this, "Laporan terkirim", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void showTab(String tab) {
        rvIngredients.setVisibility(View.GONE);
        rvSteps.setVisibility(View.GONE);
        rvReviews.setVisibility(View.GONE);
        findViewById(R.id.tvIngredientsHeader).setVisibility(View.GONE);
        findViewById(R.id.tvStepsHeader).setVisibility(View.GONE);
        findViewById(R.id.tvReviewsHeader).setVisibility(View.GONE);

        if (tab.equals("ingredients")) {
            rvIngredients.setVisibility(View.VISIBLE);
            findViewById(R.id.tvIngredientsHeader).setVisibility(View.VISIBLE);
        } else if (tab.equals("steps")) {
            rvSteps.setVisibility(View.VISIBLE);
            findViewById(R.id.tvStepsHeader).setVisibility(View.VISIBLE);
        } else if (tab.equals("reviews")) {
            rvReviews.setVisibility(View.VISIBLE);
            findViewById(R.id.tvReviewsHeader).setVisibility(View.VISIBLE);
        }
    }

    private void showIngredients() {
        rvIngredients.setAdapter(new IngredientAdapter(this, recipe.getIngredients(), false));
    }

    private void showSteps() {
        rvSteps.setAdapter(new IngredientAdapter(this, recipe.getSteps(), true));
    }

    private void showReviews() {
        Query query = db.collection("reviews")
                .whereEqualTo("recipeId", recipe.getId())
                .orderBy("createdAt", Query.Direction.DESCENDING);

        query.get().addOnSuccessListener(snapshots -> {
            List<Review> reviews = new ArrayList<>();
            for (DocumentSnapshot doc : snapshots) {
                reviews.add(doc.toObject(Review.class));
            }
            rvReviews.setAdapter(new ReviewAdapter(this, reviews));
        });
    }

    private void checkFavoriteStatus() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("bookmarks")
                .whereEqualTo("userId", uid)
                .whereEqualTo("recipeId", recipe.getId())
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!snapshots.isEmpty()) {
                        isFavorited = true;
                        bookmarkId = snapshots.getDocuments().get(0).getId();
                        btnFavorite.setImageResource(R.drawable.ic_heart_filled);
                    } else {
                        isFavorited = false;
                        btnFavorite.setImageResource(R.drawable.ic_nav_favorite);
                    }
                });
    }

    private void toggleFavorite() {
        if (checkGuest()) return;

        String uid = mAuth.getCurrentUser().getUid();

        if (isFavorited) {
            if (bookmarkId != null) {
                db.collection("bookmarks").document(bookmarkId).delete()
                        .addOnSuccessListener(aVoid -> {
                            isFavorited = false;
                            btnFavorite.setImageResource(R.drawable.ic_nav_favorite);
                            Toast.makeText(this, "Dihapus dari Favorit", Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            Bookmark bookmark = new Bookmark(uid, recipe.getId());
            db.collection("bookmarks").add(bookmark)
                    .addOnSuccessListener(docRef -> {
                        isFavorited = true;
                        bookmarkId = docRef.getId();
                        btnFavorite.setImageResource(R.drawable.ic_heart_filled);
                        Toast.makeText(this, "Ditambahkan ke Favorit", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private boolean checkGuest() {
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return false;
    }
}