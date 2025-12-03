package com.example.clickncook.controllers.admin.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
    private List<User> allUsers;
    private EditText etSearchUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_admin_users, container, false);

        View staticNav = view.findViewById(R.id.adminBottomNavContainer);
        if (staticNav != null) staticNav.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();
        etSearchUser = view.findViewById(R.id.etSearchUser); // Pastikan ID di XML sudah ditambahkan
        RecyclerView recyclerView = view.findViewById(R.id.rvUserList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        allUsers = new ArrayList<>();

        adapter = new UserAdapter(getContext(), userList, user -> {
            toggleBlockUser(user);
        });
        recyclerView.setAdapter(adapter);

        if (etSearchUser != null) {
            etSearchUser.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filter(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsers();
    }

    private void loadUsers() {
        db.collection("users").get().addOnSuccessListener(snapshots -> {
            userList.clear();
            allUsers.clear();
            for (DocumentSnapshot doc : snapshots) {
                User u = doc.toObject(User.class);
                if (u != null) {
                    u.setId(doc.getId());
                    userList.add(u);
                    allUsers.add(u);
                }
            }
            if (etSearchUser != null && etSearchUser.getText().length() > 0) {
                filter(etSearchUser.getText().toString());
            } else {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void filter(String text) {
        userList.clear();
        String query = text.toLowerCase().trim();

        if (query.isEmpty()) {
            userList.addAll(allUsers);
        } else {
            for (User item : allUsers) {
                if ((item.getName() != null && item.getName().toLowerCase().contains(query)) ||
                        (item.getEmail() != null && item.getEmail().toLowerCase().contains(query))) {
                    userList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void toggleBlockUser(User user) {
        boolean newStatus = !user.isBlocked();
        db.collection("users").document(user.getId())
                .update("isBlocked", newStatus)
                .addOnSuccessListener(aVoid -> {
                    user.setBlocked(newStatus);
                    for (User u : allUsers) {
                        if (u.getId().equals(user.getId())) {
                            u.setBlocked(newStatus);
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}