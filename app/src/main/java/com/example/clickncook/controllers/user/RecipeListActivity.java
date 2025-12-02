package com.example.clickncook.controllers.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clickncook.R;
import com.example.clickncook.models.Recipe;
import com.example.clickncook.views.adapter.RecipeAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<Recipe> recipeList;
    private FirebaseFirestore db;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        db = FirebaseFirestore.getInstance();
        tvTitle = findViewById(R.id.tvListTitle);
        recyclerView = findViewById(R.id.rvFullList);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recipeList = new ArrayList<>();
        adapter = new RecipeAdapter(this, recipeList, recipe -> {
            Intent intent = new Intent(this, DetailRecipeActivity.class);
            intent.putExtra("RECIPE_DATA", recipe);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        String type = getIntent().getStringExtra("LIST_TYPE");
        String title = getIntent().getStringExtra("LIST_TITLE");

        if (title != null) {
            tvTitle.setText(title.toUpperCase());
        }

        loadData(type);
    }

    private void loadData(String type) {
        Query query = db.collection("recipes").whereEqualTo("isDraft", false);

        if (type != null) {
            switch (type) {
                case "RECOMMENDATION":
                    query = query.orderBy("averageRating", Query.Direction.DESCENDING);
                    break;
                case "NEWEST":
                    query = query.orderBy("createdAt", Query.Direction.DESCENDING);
                    break;
                case "EASY":
                    query = query.whereEqualTo("difficulty", "Mudah");
                    break;
                case "LOCAL":
                    query = query.whereEqualTo("category", "Nusantara");
                    break;
                default:
                    query = query.whereEqualTo("category", type);
                    break;
            }
        }

        query.get().addOnSuccessListener(snapshots -> {
            recipeList.clear();
            recipeList.addAll(snapshots.toObjects(Recipe.class));
            for (int i = 0; i < snapshots.size(); i++) {
                recipeList.get(i).setId(snapshots.getDocuments().get(i).getId());
            }
            adapter.notifyDataSetChanged();
        });
    }
}