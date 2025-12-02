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

        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.rvContentList);
        EditText etSearch = view.findViewById(R.id.searchSortBar).findViewById(R.id.cardSearch).findViewById(R.id.iconSearch).getRootView().findViewById(R.id.searchSortBar).findViewWithTag("search_edit_text");

        // Manual binding karena etSearch ada di dalam CardView di dalam LinearLayout
        // Menggunakan traversal sederhana jika ID unik:
        EditText searchInput = null;
        if (view.findViewById(R.id.searchSortBar) != null) {
            // Cari EditText di hierarchy
            // Untuk mempermudah, kita asumsikan ID nya unik di layout:
            // Note: Di XML activity_admin_content, EditText tidak punya ID spesifik,
            // hanya di activity_admin_users dan activity_home yang punya ID.
            // Saya asumsikan Anda menambahkan ID @+id/etSearchContent di activity_admin_content.xml
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeList = new ArrayList<>();
        allRecipes = new ArrayList<>();

        adapter = new AdminRecipeAdapter(getContext(), recipeList, recipe -> {
            deleteRecipe(recipe);
        });
        recyclerView.setAdapter(adapter);

        loadContent();

        return view;
    }

    private void loadContent() {
        db.collection("recipes")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {
                    allRecipes.clear();
                    recipeList.clear();
                    for (DocumentSnapshot doc : snapshots) {
                        Recipe r = doc.toObject(Recipe.class);
                        r.setId(doc.getId());
                        allRecipes.add(r);
                        recipeList.add(r);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void deleteRecipe(Recipe recipe) {
        db.collection("recipes").document(recipe.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Resep dihapus", Toast.LENGTH_SHORT).show();
                    loadContent();
                });
    }
}