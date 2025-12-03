package com.example.clickncook.controllers.user.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clickncook.R;
import com.example.clickncook.models.Review;
import com.example.clickncook.views.adapter.ReviewHistoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ReviewHistoryFragment extends Fragment {

    private FirebaseFirestore db;
    private ReviewHistoryAdapter adapter;
    private List<Review> reviewList;
    private LinearLayout emptyState;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_review_history, container, false);

        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.rvReviewHistory);
        emptyState = view.findViewById(R.id.layoutEmptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reviewList = new ArrayList<>();
        adapter = new ReviewHistoryAdapter(getContext(), reviewList, review -> {
            deleteReview(review);
        });
        recyclerView.setAdapter(adapter);

        loadReviews();

        return view;
    }

    private void loadReviews() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("reviews")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(snapshots -> {
                    reviewList.clear();
                    for (DocumentSnapshot doc : snapshots) {
                        Review r = doc.toObject(Review.class);
                        r.setId(doc.getId());
                        reviewList.add(r);
                    }
                    adapter.notifyDataSetChanged();

                    if (reviewList.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyState.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void deleteReview(Review review) {
        db.collection("reviews").document(review.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    reviewList.remove(review);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Ulasan dihapus", Toast.LENGTH_SHORT).show();
                    if (reviewList.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
    }
}