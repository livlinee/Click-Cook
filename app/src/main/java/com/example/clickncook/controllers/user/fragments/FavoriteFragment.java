package com.example.clickncook.controllers.user.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clickncook.R;
import com.example.clickncook.controllers.user.DetailRecipeActivity;
import com.example.clickncook.models.Recipe;
import com.example.clickncook.views.adapter.RecipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private FirebaseFirestore db;
    private RecipeAdapter adapter;
    private List<Recipe> favoriteList;
    private View emptyState;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_favorite, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.rvFavoriteGrid);
        emptyState = view.findViewById(R.id.layoutEmptyState);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        favoriteList = new ArrayList<>();
        adapter = new RecipeAdapter(getContext(), favoriteList, recipe -> {
            Intent intent = new Intent(getContext(), DetailRecipeActivity.class);
            intent.putExtra("RECIPE_DATA", recipe);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadFavorites();

        return view;
    }

    private void loadFavorites() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("bookmarks").whereEqualTo("userId", uid).get()
                .addOnSuccessListener(snapshots -> {
                    List<String> recipeIds = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots) {
                        recipeIds.add(doc.getString("recipeId"));
                    }

                    if (!recipeIds.isEmpty()) {
                        db.collection("recipes")
                                .whereIn(FieldPath.documentId(), recipeIds)
                                .get()
                                .addOnSuccessListener(recipeSnapshots -> {
                                    favoriteList.clear();
                                    for (DocumentSnapshot doc : recipeSnapshots) {
                                        Recipe r = doc.toObject(Recipe.class);
                                        r.setId(doc.getId());
                                        favoriteList.add(r);
                                    }
                                    adapter.notifyDataSetChanged();
                                    updateUI();
                                });
                    } else {
                        favoriteList.clear();
                        adapter.notifyDataSetChanged();
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        if (favoriteList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}