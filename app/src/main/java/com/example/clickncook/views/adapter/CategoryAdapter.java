package com.example.clickncook.views.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clickncook.R;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<String> categories;
    private OnCategoryClick listener;
    private int selectedPos = -1;

    public interface OnCategoryClick {
        void onClick(String category);
    }

    public CategoryAdapter(List<String> categories, OnCategoryClick listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);
        holder.btn.setText(category);

        if (selectedPos == position) {
            holder.btn.setBackgroundColor(Color.parseColor("#FF9800")); // Orange
            holder.btn.setTextColor(Color.WHITE);
        } else {
            holder.btn.setBackgroundColor(Color.TRANSPARENT);
            holder.btn.setTextColor(Color.parseColor("#FF9800"));
        }

        holder.btn.setOnClickListener(v -> {
            int previousPos = selectedPos;
            selectedPos = holder.getAdapterPosition();
            notifyItemChanged(previousPos);
            notifyItemChanged(selectedPos);
            listener.onClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialButton btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id.btn_category);
        }
    }
}