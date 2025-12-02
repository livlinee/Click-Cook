package com.example.clickncook.controllers.user.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.clickncook.R;
import com.example.clickncook.controllers.user.AddRecipeActivity;
import com.example.clickncook.controllers.user.DetailRecipeActivity;
import com.example.clickncook.controllers.user.SettingsActivity;
import com.example.clickncook.models.Recipe;
import com.example.clickncook.views.adapter.RecipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FirebaseFirestore db;
    private RecipeAdapter adapterPublished, adapterDraft;
    private List<Recipe> listPublished, listDraft;
    private String currentUserId;

    private RecyclerView rvPublished, rvDraft;
    private LinearLayout layoutEmptyPublished, layoutEmptyDraft;
    private TextView tvTabPub, tvTabDraft;
    private View indicatorPub, indicatorDraft;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ImageView imgAvatar = view.findViewById(R.id.imgProfile);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvBio = view.findViewById(R.id.tvBio);

        LinearLayout tabPublished = view.findViewById(R.id.tabPublished);
        LinearLayout tabDraft = view.findViewById(R.id.tabDraft);
        tvTabPub = view.findViewById(R.id.tvPublished);
        tvTabDraft = view.findViewById(R.id.tvDraft);
        indicatorPub = view.findViewById(R.id.indicatorPublished);
        indicatorDraft = view.findViewById(R.id.indicatorDraft);

        rvPublished = view.findViewById(R.id.rvPublishedGrid);
        rvDraft = view.findViewById(R.id.rvDraftGrid);
        layoutEmptyPublished = view.findViewById(R.id.layoutEmptyPublished);
        layoutEmptyDraft = view.findViewById(R.id.layoutEmptyDraft);

        view.findViewById(R.id.btnSettings).setOnClickListener(v -> startActivity(new Intent(getContext(), SettingsActivity.class)));
        view.findViewById(R.id.btnCreateRecipeBottom).setOnClickListener(v -> startActivity(new Intent(getContext(), AddRecipeActivity.class)));

        db.collection("users").document(currentUserId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                tvName.setText(doc.getString("name"));
                tvBio.setText(doc.getString("bio"));
                String photoUrl = doc.getString("photoUrl");
                if (photoUrl != null) Glide.with(this).load(photoUrl).circleCrop().into(imgAvatar);
            }
        });

        setupRecyclers();

        tabPublished.setOnClickListener(v -> switchTab(true));
        tabDraft.setOnClickListener(v -> switchTab(false));

        loadRecipes(false);
        loadRecipes(true);

        return view;
    }

    private void setupRecyclers() {
        rvPublished.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvDraft.setLayoutManager(new GridLayoutManager(getContext(), 2));

        listPublished = new ArrayList<>();
        listDraft = new ArrayList<>();

        adapterPublished = new RecipeAdapter(getContext(), listPublished, this::openDetail);
        adapterDraft = new RecipeAdapter(getContext(), listDraft, this::openDetail);

        rvPublished.setAdapter(adapterPublished);
        rvDraft.setAdapter(adapterDraft);
    }

    private void openDetail(Recipe recipe) {
        Intent intent = new Intent(getContext(), DetailRecipeActivity.class);
        intent.putExtra("RECIPE_DATA", recipe);
        startActivity(intent);
    }

    private void switchTab(boolean isPublished) {
        int activeColor = ContextCompat.getColor(getContext(), R.color.primary_orange);
        int inactiveColor = ContextCompat.getColor(getContext(), R.color.gray_text);
        int inactiveStroke = ContextCompat.getColor(getContext(), R.color.gray_stroke);

        if (isPublished) {
            tvTabPub.setTextColor(activeColor);
            indicatorPub.setBackgroundColor(activeColor);
            tvTabDraft.setTextColor(inactiveColor);
            indicatorDraft.setBackgroundColor(inactiveStroke);

            updateListVisibility(listPublished.isEmpty(), true);
        } else {
            tvTabPub.setTextColor(inactiveColor);
            indicatorPub.setBackgroundColor(inactiveStroke);
            tvTabDraft.setTextColor(activeColor);
            indicatorDraft.setBackgroundColor(activeColor);

            updateListVisibility(listDraft.isEmpty(), false);
        }
    }

    private void updateListVisibility(boolean isEmpty, boolean isPublishedTab) {
        rvPublished.setVisibility(View.GONE);
        rvDraft.setVisibility(View.GONE);
        layoutEmptyPublished.setVisibility(View.GONE);
        layoutEmptyDraft.setVisibility(View.GONE);

        if (isPublishedTab) {
            if (isEmpty) layoutEmptyPublished.setVisibility(View.VISIBLE);
            else rvPublished.setVisibility(View.VISIBLE);
        } else {
            if (isEmpty) layoutEmptyDraft.setVisibility(View.VISIBLE);
            else rvDraft.setVisibility(View.VISIBLE);
        }
    }

    private void loadRecipes(boolean isDraft) {
        db.collection("recipes")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("isDraft", isDraft)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;

                    List<Recipe> targetList = isDraft ? listDraft : listPublished;
                    RecipeAdapter targetAdapter = isDraft ? adapterDraft : adapterPublished;

                    targetList.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots) {
                            Recipe r = doc.toObject(Recipe.class);
                            r.setId(doc.getId());
                            targetList.add(r);
                        }
                    }
                    targetAdapter.notifyDataSetChanged();

                    boolean isPubTabActive = indicatorPub.getSolidColor() == ContextCompat.getColor(getContext(), R.color.primary_orange); // Simplifikasi logic cek
                    if(tvTabPub.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.primary_orange)) {
                        switchTab(true);
                    } else {
                        switchTab(false);
                    }
                });
    }
}