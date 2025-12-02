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
import com.example.clickncook.models.User;
import com.example.clickncook.views.adapter.UserAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class AdminUsersFragment extends Fragment {

    private FirebaseFirestore db;
    private UserAdapter adapter;
    private List<User> userList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_admin_users, container, false);

        View staticNav = view.findViewById(R.id.adminBottomNavContainer);
        if (staticNav != null) staticNav.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.rvUserList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        adapter = new UserAdapter(getContext(), userList, user -> {
            toggleBlockUser(user);
        });
        recyclerView.setAdapter(adapter);

        loadUsers();

        return view;
    }

    private void loadUsers() {
        db.collection("users").get().addOnSuccessListener(snapshots -> {
            userList.clear();
            for (DocumentSnapshot doc : snapshots) {
                User u = doc.toObject(User.class);
                if (u != null) {
                    u.setId(doc.getId());
                    userList.add(u);
                }
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void toggleBlockUser(User user) {
        boolean newStatus = !user.isBlocked();
        db.collection("users").document(user.getId())
                .update("isBlocked", newStatus)
                .addOnSuccessListener(aVoid -> {
                    user.setBlocked(newStatus);
                    adapter.notifyDataSetChanged();
                });
    }
}