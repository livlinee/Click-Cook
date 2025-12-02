package com.example.clickncook.controllers.admin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.clickncook.R;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminHomeFragment extends Fragment {

    private TextView tvUserCount, tvRecipeCount;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        db = FirebaseFirestore.getInstance();
        tvUserCount = view.findViewById(R.id.tv_total_users);
        tvRecipeCount = view.findViewById(R.id.tv_total_recipes);

        loadStats();

        return view;
    }

    private void loadStats() {
        db.collection("users").count().get(AggregateSource.SERVER).addOnSuccessListener(snapshot -> {
            tvUserCount.setText(String.valueOf(snapshot.getCount()));
        });

        db.collection("recipes").count().get(AggregateSource.SERVER).addOnSuccessListener(snapshot -> {
            tvRecipeCount.setText(String.valueOf(snapshot.getCount()));
        });
    }
}