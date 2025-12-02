package com.example.clickncook.controllers.user.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
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
import com.example.clickncook.views.adapter.RecipeAdapter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db;
    private TextView tvGreeting;
    private EditText etSearch;
    private ProgressBar progressBar;
    private View mainContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        tvGreeting = view.findViewById(R.id.tvWelcome);
        etSearch = view.findViewById(R.id.etSearch);
        progressBar = view.findViewById(R.id.progressBar);
        mainContent = view.findViewById(R.id.mainContentScroll);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Button btnLogin = view.findViewById(R.id.btnLoginRegister);

        if (user != null) {
            tvGreeting.setText("Halo, " + (user.getDisplayName() != null ? user.getDisplayName() : "Chef") + "!");
            btnLogin.setVisibility(View.GONE);
        } else {
            tvGreeting.setText("Selamat Datang!");
            btnLogin.setVisibility(View.VISIBLE);
            btnLogin.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        }

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                performSearch();
                return true;
            }
            return false;
        });

        view.findViewById(R.id.iconSearch).setOnClickListener(v -> performSearch());

        view.findViewById(R.id.chipAll).setOnClickListener(v -> openListActivity("Semua", "SEMUA RESEP"));
        view.findViewById(R.id.chipNusantara).setOnClickListener(v -> openListActivity("Nusantara", "MASAKAN NUSANTARA"));
        view.findViewById(R.id.chipWestern).setOnClickListener(v -> openListActivity("Western", "RESEP WESTERN"));
        view.findViewById(R.id.chipDessert).setOnClickListener(v -> openListActivity("Dessert", "ANEKA DESSERT"));
        view.findViewById(R.id.chipMinuman).setOnClickListener(v -> openListActivity("Minuman", "ANEKA MINUMAN"));
        view.findViewById(R.id.chipHealthy).setOnClickListener(v -> openListActivity("Healthy", "MAKANAN SEHAT"));

        loadAllSections(view);

        return view;
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        if (!query.isEmpty()) {
            Intent intent = new Intent(getContext(), RecipeListActivity.class);
            intent.putExtra("SEARCH_QUERY", query);
            startActivity(intent);
        }
    }

    private void loadAllSections(View view) {
        progressBar.setVisibility(View.VISIBLE);
        if(mainContent != null) mainContent.setVisibility(View.INVISIBLE);

        Task<QuerySnapshot> taskRec = getQuery("RECOMMENDATION").get();
        Task<QuerySnapshot> taskNew = getQuery("NEWEST").get();
        Task<QuerySnapshot> taskEasy = getQuery("EASY").get();
        Task<QuerySnapshot> taskLocal = getQuery("LOCAL").get();
        Task<QuerySnapshot> taskWestern = getQuery("Western").get();
        Task<QuerySnapshot> taskDessert = getQuery("Dessert").get();
        Task<QuerySnapshot> taskDrinks = getQuery("Minuman").get();
        Task<QuerySnapshot> taskHealthy = getQuery("Healthy").get();

        Tasks.whenAllSuccess(taskRec, taskNew, taskEasy, taskLocal, taskWestern, taskDessert, taskDrinks, taskHealthy)
                .addOnSuccessListener(results -> {
                    setupRecycler(view, R.id.rvRecommendation, R.id.linkSeeMoreRec, "RECOMMENDATION", "Rekomendasi", (QuerySnapshot) results.get(0));
                    setupRecycler(view, R.id.rvNewest, R.id.linkSeeMoreNew, "NEWEST", "Resep Terbaru", (QuerySnapshot) results.get(1));
                    setupRecycler(view, R.id.rvEasy, R.id.linkSeeMoreEasy, "EASY", "Resep Praktis", (QuerySnapshot) results.get(2));
                    setupRecycler(view, R.id.rvLocal, R.id.linkSeeMoreLocal, "LOCAL", "Masakan Nusantara", (QuerySnapshot) results.get(3));
                    setupRecycler(view, R.id.rvWestern, R.id.linkSeeMoreWestern, "Western", "Resep Western", (QuerySnapshot) results.get(4));
                    setupRecycler(view, R.id.rvDessert, R.id.linkSeeMoreDessert, "Dessert", "Aneka Dessert", (QuerySnapshot) results.get(5));
                    setupRecycler(view, R.id.rvDrinks, R.id.linkSeeMoreDrinks, "Minuman", "Aneka Minuman", (QuerySnapshot) results.get(6));
                    setupRecycler(view, R.id.rvHealthy, R.id.linkSeeMoreHealthy, "Healthy", "Makanan Sehat", (QuerySnapshot) results.get(7));

                    progressBar.setVisibility(View.GONE);
                    if(mainContent != null) mainContent.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    if(getContext() != null)
                        Toast.makeText(getContext(), "Gagal memuat data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private Query getQuery(String type) {
        Query query = db.collection("recipes").whereEqualTo("isDraft", false);

        if (type.equals("RECOMMENDATION")) return query.orderBy("averageRating", Query.Direction.DESCENDING).limit(5);
        if (type.equals("NEWEST")) return query.orderBy("createdAt", Query.Direction.DESCENDING).limit(5);
        if (type.equals("EASY")) return query.whereEqualTo("difficulty", "Mudah").limit(5);
        if (type.equals("LOCAL")) return query.whereEqualTo("category", "Nusantara").limit(5);

        return query.whereEqualTo("category", type).limit(5);
    }

    private void setupRecycler(View view, int recyclerId, int linkId, String type, String title, QuerySnapshot snapshot) {
        RecyclerView recyclerView = view.findViewById(recyclerId);
        TextView tvSeeMore = view.findViewById(linkId);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<Recipe> list = new ArrayList<>();

        if(snapshot != null) {
            list.addAll(snapshot.toObjects(Recipe.class));
            for (int i = 0; i < snapshot.size(); i++) {
                list.get(i).setId(snapshot.getDocuments().get(i).getId());
            }
        }

        RecipeAdapter adapter = new RecipeAdapter(getContext(), list, recipe -> {
            Intent intent = new Intent(getContext(), DetailRecipeActivity.class);
            intent.putExtra("RECIPE_DATA", recipe);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        tvSeeMore.setOnClickListener(v -> openListActivity(type, title));
    }

    private void openListActivity(String type, String title) {
        Intent intent = new Intent(getContext(), RecipeListActivity.class);
        intent.putExtra("LIST_TYPE", type);
        intent.putExtra("LIST_TITLE", title);
        startActivity(intent);
    }
}