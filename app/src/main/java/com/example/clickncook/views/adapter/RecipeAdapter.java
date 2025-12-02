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

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private Context context;
    private List<Recipe> recipeList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    public RecipeAdapter(Context context, List<Recipe> recipeList, OnItemClickListener listener) {
        this.context = context;
        this.recipeList = recipeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.tvTitle.setText(recipe.getTitle());
        holder.tvTime.setText(recipe.getCookTime());
        holder.tvRating.setText(String.format("%.1f ★", recipe.getAverageRating()));

        if (recipe.getImageUrl() != null) {
            Glide.with(context)
                    .load(recipe.getImageUrl())
                    .centerCrop()
                    .into(holder.imgThumbnail);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(recipe));
    }

    @Override
    public int getItemCount() { return recipeList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvTitle, tvRating, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_recipe_thumbnail);
            tvTitle = itemView.findViewById(R.id.tv_recipe_title);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}