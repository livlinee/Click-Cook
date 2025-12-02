package com.example.clickncook.controllers.admin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clickncook.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_admin_dashboard, container, false);

        View staticNav = view.findViewById(R.id.adminBottomNavContainer);
        if (staticNav != null) staticNav.setVisibility(View.GONE);

        RecyclerView rvActivity = view.findViewById(R.id.rvActivityLog);
        rvActivity.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}