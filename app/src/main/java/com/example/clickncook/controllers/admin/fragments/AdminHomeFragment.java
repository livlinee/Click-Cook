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

    private FirebaseFirestore db;
    private TextView tvTotalUsers, tvTotalRecipes, tvTotalReports;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_admin_dashboard, container, false);

        View staticNav = view.findViewById(R.id.adminBottomNavContainer);
        if (staticNav != null) staticNav.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();

        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        tvTotalRecipes = view.findViewById(R.id.tvTotalRecipes);
        tvTotalReports = view.findViewById(R.id.tvTotalReports);

        loadDashboardData();

        return view;
    }

    private void loadDashboardData() {
        db.collection("users").count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(snapshot -> {
                    if (tvTotalUsers != null) {
                        tvTotalUsers.setText(String.valueOf(snapshot.getCount()));
                    }
                });

        db.collection("recipes")
                .whereEqualTo("isDraft", false)
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(snapshot -> {
                    if (tvTotalRecipes != null) {
                        tvTotalRecipes.setText(String.valueOf(snapshot.getCount()));
                    }
                });

        db.collection("reports")
                .whereEqualTo("status", "Pending")
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(snapshot -> {
                    long count = snapshot.getCount();
                    if (tvTotalReports != null) {
                        if (count > 0) {
                            tvTotalReports.setText(count + " Laporan Perlu Ditinjau");
                        } else {
                            tvTotalReports.setText("Tidak ada laporan baru");
                        }
                    }
                });
    }
}