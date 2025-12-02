package com.example.clickncook.controllers.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clickncook.R;
import com.example.clickncook.models.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class WriteReviewActivity extends AppCompatActivity {

    private String recipeId, recipeTitle, recipeImg;
    private RatingBar ratingBar;
    private EditText etComment;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        db = FirebaseFirestore.getInstance();

        recipeId = getIntent().getStringExtra("RECIPE_ID");
        recipeTitle = getIntent().getStringExtra("RECIPE_TITLE");
        recipeImg = getIntent().getStringExtra("RECIPE_IMG");

        ratingBar = findViewById(R.id.rating_bar_input);
        etComment = findViewById(R.id.et_review_comment);
        Button btnSubmit = findViewById(R.id.btn_submit_review);

        btnSubmit.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String comment = etComment.getText().toString();

        if (rating == 0) {
            Toast.makeText(this, "Mohon beri rating bintang", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Review review = new Review();
        review.setUserId(user.getUid());
        review.setUserName(user.getDisplayName());
        review.setUserPhotoUrl(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
        review.setRecipeId(recipeId);
        review.setRecipeTitle(recipeTitle);
        review.setRecipeImageUrl(recipeImg);
        review.setRating(rating);
        review.setComment(comment);

        final DocumentReference recipeRef = db.collection("recipes").document(recipeId);
        final DocumentReference reviewRef = db.collection("reviews").document(); // ID baru auto

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(recipeRef);

                double oldTotalRating = snapshot.getDouble("averageRating") != null ? snapshot.getDouble("averageRating") : 0.0;
                long oldNumReviews = snapshot.getLong("totalReviews") != null ? snapshot.getLong("totalReviews") : 0;

                long newNumReviews = oldNumReviews + 1;
                double newAverage = ((oldTotalRating * oldNumReviews) + rating) / newNumReviews;

                transaction.update(recipeRef, "totalReviews", newNumReviews);
                transaction.update(recipeRef, "averageRating", newAverage);

                transaction.set(reviewRef, review);

                return null;
            }
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Ulasan Terkirim!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal mengirim ulasan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}