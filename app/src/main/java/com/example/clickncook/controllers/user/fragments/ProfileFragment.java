package com.example.clickncook.controllers.user.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.clickncook.R;
import com.example.clickncook.controllers.auth.LoginActivity;
import com.example.clickncook.controllers.user.AddRecipeActivity;
import com.example.clickncook.controllers.user.DetailRecipeActivity;
import com.example.clickncook.controllers.user.SettingsActivity;
import com.example.clickncook.models.Recipe;
import com.example.clickncook.views.adapter.RecipeAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FirebaseFirestore db;
    private RecipeAdapter adapter;
    private List<Recipe> recipeList;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ImageView imgAvatar = view.findViewById(R.id.img_profile);
        TextView tvName = view.findViewById(R.id.tv_profile_name);
        TextView tvBio = view.findViewById(R.id.tv_profile_bio);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_profile_recipes);

        view.findViewById(R.id.btn_settings).setOnClickListener(v -> startActivity(new Intent(getContext(), SettingsActivity.class)));
        view.findViewById(R.id.btn_create_new).setOnClickListener(v -> startActivity(new Intent(getContext(), AddRecipeActivity.class)));

        db.collection("users").document(currentUserId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                tvName.setText(doc.getString("name"));
                tvBio.setText(doc.getString("bio"));
                String photoUrl = doc.getString("photoUrl");
                if (photoUrl != null) Glide.with(this).load(photoUrl).circleCrop().into(imgAvatar);
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recipeList = new ArrayList<>();
        adapter = new RecipeAdapter(getContext(), recipeList, recipe -> {
            Intent intent = new Intent(getContext(), DetailRecipeActivity.class);
            intent.putExtra("RECIPE_DATA", recipe);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadRecipes(false);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadRecipes(tab.getPosition() == 1);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    private void loadRecipes(boolean isDraft) {
        db.collection("recipes")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("isDraft", isDraft)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {
                    recipeList.clear();
                    for (DocumentSnapshot doc : snapshots) {
                        Recipe r = doc.toObject(Recipe.class);
                        r.setId(doc.getId());
                        recipeList.add(r);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}