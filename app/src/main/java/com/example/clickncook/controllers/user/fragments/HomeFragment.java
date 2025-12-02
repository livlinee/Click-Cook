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
import com.example.clickncook.controllers.user.RecipeListActivity;
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
    private TextView tvGreeting;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        db = FirebaseFirestore.getInstance();
        tvGreeting = view.findViewById(R.id.tvWelcome);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        View btnLogin = view.findViewById(R.id.btnLoginRegister);
        if (user != null) {
            tvGreeting.setText("Halo, " + (user.getDisplayName() != null ? user.getDisplayName() : "Chef") + "!");
            btnLogin.setVisibility(View.GONE);
        } else {
            tvGreeting.setText("Selamat Datang!");
            btnLogin.setVisibility(View.VISIBLE);
            btnLogin.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        }
        view.findViewById(R.id.chipAll).setOnClickListener(v -> openListActivity("Semua", "SEMUA RESEP"));

        setupSection(view, R.id.rvRecommendation, R.id.linkSeeMoreRec, "RECOMMENDATION", "Rekomendasi Pilihan");
        setupSection(view, R.id.rvNewest, R.id.linkSeeMoreNew, "NEWEST", "Resep Terbaru");
        setupSection(view, R.id.rvEasy, R.id.linkSeeMoreEasy, "EASY", "Resep Praktis");
        setupSection(view, R.id.rvLocal, R.id.linkSeeMoreLocal, "LOCAL", "Masakan Nusantara");

        return view;
    }

    private void setupSection(View view, int recyclerId, int linkId, String type, String title) {
        RecyclerView recyclerView = view.findViewById(recyclerId);
        TextView tvSeeMore = view.findViewById(linkId);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<Recipe> list = new ArrayList<>();
        RecipeAdapter adapter = new RecipeAdapter(getContext(), list, recipe -> {
            Intent intent = new Intent(getContext(), DetailRecipeActivity.class);
            intent.putExtra("RECIPE_DATA", recipe);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        Query query = db.collection("recipes").whereEqualTo("isDraft", false);

        if (type.equals("RECOMMENDATION")) query = query.orderBy("averageRating", Query.Direction.DESCENDING);
        else if (type.equals("NEWEST")) query = query.orderBy("createdAt", Query.Direction.DESCENDING);
        else if (type.equals("EASY")) query = query.whereEqualTo("difficulty", "Mudah");
        else if (type.equals("LOCAL")) query = query.whereEqualTo("category", "Nusantara");

        query.limit(5).get().addOnSuccessListener(snapshots -> {
            list.clear();
            list.addAll(snapshots.toObjects(Recipe.class));
            for (int i = 0; i < snapshots.size(); i++) {
                list.get(i).setId(snapshots.getDocuments().get(i).getId());
            }
            adapter.notifyDataSetChanged();
        });

        tvSeeMore.setOnClickListener(v -> openListActivity(type, title));
    }

    private void openListActivity(String type, String title) {
        Intent intent = new Intent(getContext(), RecipeListActivity.class);
        intent.putExtra("LIST_TYPE", type);
        intent.putExtra("LIST_TITLE", title);
        startActivity(intent);
    }
}