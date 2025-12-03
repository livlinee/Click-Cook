package com.example.clickncook.controllers.admin.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clickncook.R;
import com.example.clickncook.models.Recipe;
import com.example.clickncook.views.adapter.AdminRecipeAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class AdminContentFragment extends Fragment {

    private FirebaseFirestore db;
    private AdminRecipeAdapter adapter;
    private List<Recipe> recipeList;
    private List<Recipe> allRecipes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_admin_content, container, false);

        View staticNav = view.findViewById(R.id.adminBottomNavContainer);
        if (staticNav != null) staticNav.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.rvContentList);
        EditText etSearch = view.findViewById(R.id.etSearchContent);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeList = new ArrayList<>();
        allRecipes = new ArrayList<>();

        adapter = new AdminRecipeAdapter(getContext(), recipeList, recipe -> {
            deleteRecipe(recipe);
        });
        recyclerView.setAdapter(adapter);

        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filter(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        loadContent();

        return view;
    }

    private void loadContent() {
        db.collection("recipes")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        if (getContext() != null)
                            Toast.makeText(getContext(), "Gagal memuat data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    allRecipes.clear();
                    recipeList.clear();

                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots) {
                            Recipe r = doc.toObject(Recipe.class);
                            if (r != null) {
                                r.setId(doc.getId());
                                allRecipes.add(r);
                                recipeList.add(r);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void filter(String text) {
        recipeList.clear();
        if (text.isEmpty()) {
            recipeList.addAll(allRecipes);
        } else {
            text = text.toLowerCase();
            for (Recipe item : allRecipes) {
                if (item.getTitle().toLowerCase().contains(text) ||
                        (item.getUserName() != null && item.getUserName().toLowerCase().contains(text))) {
                    recipeList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void deleteRecipe(Recipe recipe) {
        db.collection("recipes").document(recipe.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Resep berhasil dihapus", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Gagal menghapus: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}