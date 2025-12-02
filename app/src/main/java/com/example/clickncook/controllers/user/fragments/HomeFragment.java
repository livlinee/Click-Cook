package com.example.clickncook.controllers.user.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clickncook.R;
import com.example.clickncook.controllers.auth.LoginActivity;
import com.example.clickncook.controllers.user.DetailRecipeActivity;
import com.example.clickncook.models.Recipe;
import com.example.clickncook.views.adapter.CategoryAdapter;
import com.example.clickncook.views.adapter.RecipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private TextView tvGreeting;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        tvGreeting = view.findViewById(R.id.tv_greeting);

        // 1. Setup Header Greeting
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvGreeting.setText("Halo, " + (user.getDisplayName() != null ? user.getDisplayName() : "Chef") + "!");
        } else {
            tvGreeting.setText("Selamat Datang!");
            view.findViewById(R.id.btn_login_register).setVisibility(View.VISIBLE); // Tombol Login khusus Guest
            view.findViewById(R.id.btn_login_register).setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        }

        RecyclerView recyclerCategory = view.findViewById(R.id.recycler_categories);
        recyclerCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<String> categories = Arrays.asList("Semua", "Sarapan", "Dessert", "Minuman", "Dinner");
        recyclerCategory.setAdapter(new CategoryAdapter(categories, category -> {
            loadRecipes(category);
        }));

        // 3. Setup List Resep
        RecyclerView recyclerRecipe = view.findViewById(R.id.recycler_recipes);
        recyclerRecipe.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(getContext(), recipeList, recipe -> {
            Intent intent = new Intent(getContext(), DetailRecipeActivity.class);
            intent.putExtra("RECIPE_DATA", recipe);
            startActivity(intent);
        });
        recyclerRecipe.setAdapter(recipeAdapter);

        loadRecipes("Semua");

        return view;
    }

    private void loadRecipes(String category) {
        Query query = db.collection("recipes")
                .whereEqualTo("isDraft", false)
                .orderBy("createdAt", Query.Direction.DESCENDING);

        if (!category.equals("Semua")) {
            query = query.whereEqualTo("category", category);
        }

        query.get().addOnSuccessListener(snapshots -> {
            recipeList.clear();
            recipeList.addAll(snapshots.toObjects(Recipe.class));
            for (int i = 0; i < snapshots.size(); i++) {
                recipeList.get(i).setId(snapshots.getDocuments().get(i).getId());
            }
            recipeAdapter.notifyDataSetChanged();
        });
    }
}