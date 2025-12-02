package com.example.clickncook.controllers.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.clickncook.models.Review;
import com.example.clickncook.views.adapter.IngredientAdapter;
import com.example.clickncook.views.adapter.ReviewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class DetailRecipeActivity extends AppCompatActivity {

    private Recipe recipe;
    private RecyclerView recyclerView;
    private ImageView btnFavorite;
    private boolean isFavorited = false;
    private String bookmarkId = null;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recipe);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recipe = (Recipe) getIntent().getSerializableExtra("RECIPE_DATA");

        // Binding Views
        ImageView imgCover = findViewById(R.id.img_cover);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvAuthor = findViewById(R.id.tv_author);
        TextView tvRating = findViewById(R.id.tv_rating_summary);
        RatingBar ratingBar = findViewById(R.id.rating_bar_display);
        btnFavorite = findViewById(R.id.btn_favorite_heart);

        if (recipe != null) {
            tvTitle.setText(recipe.getTitle());
            tvAuthor.setText("Oleh: " + recipe.getUserName());
            tvRating.setText(String.format("%.1f", recipe.getAverageRating()));
            ratingBar.setRating((float) recipe.getAverageRating());
            Glide.with(this).load(recipe.getImageUrl()).into(imgCover);

            if (mAuth.getCurrentUser() != null) checkFavoriteStatus();
        }

        recyclerView = findViewById(R.id.recycler_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        findViewById(R.id.btn_ingredients).setOnClickListener(v -> showIngredients());
        findViewById(R.id.btn_steps).setOnClickListener(v -> showSteps());
        findViewById(R.id.btn_reviews).setOnClickListener(v -> showReviews());

        showIngredients();

        btnFavorite.setOnClickListener(v -> toggleFavorite());

        findViewById(R.id.btn_write_review).setOnClickListener(v -> {
            if (checkGuest()) return;
            Intent intent = new Intent(this, WriteReviewActivity.class);
            intent.putExtra("RECIPE_ID", recipe.getId());
            intent.putExtra("RECIPE_TITLE", recipe.getTitle());
            intent.putExtra("RECIPE_IMG", recipe.getImageUrl());
            startActivity(intent);
        });

        findViewById(R.id.btn_report_exclamation).setOnClickListener(v -> {
            if (checkGuest()) return;
            Toast.makeText(this, "Fitur Lapor akan segera hadir", Toast.LENGTH_SHORT).show();
        });
    }

    private void showIngredients() {
        recyclerView.setAdapter(new IngredientAdapter(this, recipe.getIngredients(), false));
    }

    private void showSteps() {
        recyclerView.setAdapter(new IngredientAdapter(this, recipe.getSteps(), true));
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
            recyclerView.setAdapter(new ReviewAdapter(this, reviews));

            if (reviews.isEmpty()) {
                Toast.makeText(this, "Belum ada ulasan.", Toast.LENGTH_SHORT).show();
            }
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
                        btnFavorite.setImageResource(R.drawable.ic_heart_outline);
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
                            btnFavorite.setImageResource(R.drawable.ic_heart_outline);
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