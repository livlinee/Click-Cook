package com.example.clickncook.views.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.clickncook.R;
import com.example.clickncook.models.Recipe;
import java.util.List;

public class AdminRecipeAdapter extends RecyclerView.Adapter<AdminRecipeAdapter.ViewHolder> {

    private Context context;
    private List<Recipe> recipeList;
    private OnDeleteClickListener listener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Recipe recipe);
    }

    public AdminRecipeAdapter(Context context, List<Recipe> recipeList, OnDeleteClickListener listener) {
        this.context = context;
        this.recipeList = recipeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_recipe_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.tvTitle.setText(recipe.getTitle());
        holder.tvAuthor.setText("Oleh: " + recipe.getUserName());

        holder.tvDate.setText("Diposting: Baru saja");

        if (recipe.getImageUrl() != null) {
            Glide.with(context)
                    .load(recipe.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.logo)
                    .into(holder.imgThumb);
        }

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(recipe));
    }

    @Override
    public int getItemCount() { return recipeList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvTitle, tvAuthor, tvDate;
        View btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgRecipeThumb);
            tvTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvAuthor = itemView.findViewById(R.id.tvRecipeAuthor);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}