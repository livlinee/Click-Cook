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

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Recipe> recipeList;
    private OnItemClickListener listener;

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_DRAFT = 1;

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    public RecipeAdapter(Context context, List<Recipe> recipeList, OnItemClickListener listener) {
        this.context = context;
        this.recipeList = recipeList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return recipeList.get(position).isDraft() ? TYPE_DRAFT : TYPE_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_DRAFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_draft_recipe, parent, false);
            return new DraftViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_card_vertical, parent, false);
            return new NormalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        if (getItemViewType(position) == TYPE_DRAFT) {
            DraftViewHolder draftHolder = (DraftViewHolder) holder;
            draftHolder.tvTitle.setText(recipe.getTitle());
            draftHolder.tvStatus.setText("Draf belum diterbitkan");

            if (recipe.getImageUrl() != null) {
                Glide.with(context).load(recipe.getImageUrl()).centerCrop().into(draftHolder.imgThumb);
            }

            draftHolder.itemView.setOnClickListener(v -> listener.onItemClick(recipe));

        } else {
            NormalViewHolder normalHolder = (NormalViewHolder) holder;
            normalHolder.tvTitle.setText(recipe.getTitle());
            normalHolder.tvScore.setText(String.format("%.1f", recipe.getAverageRating()));
            normalHolder.tvReviewCount.setText("(" + recipe.getTotalReviews() + " Ulasan)");
            normalHolder.tvAuthor.setText(recipe.getUserName());

            if (recipe.getImageUrl() != null) {
                Glide.with(context).load(recipe.getImageUrl()).centerCrop().into(normalHolder.imgThumb);
            }

            normalHolder.itemView.setOnClickListener(v -> listener.onItemClick(recipe));
        }
    }

    @Override
    public int getItemCount() { return recipeList.size(); }

    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvTitle, tvScore, tvReviewCount, tvAuthor;

        public NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgRecipeThumb);
            tvTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvReviewCount = itemView.findViewById(R.id.tvReviewCount);
            tvAuthor = itemView.findViewById(R.id.tvAuthorName);
        }
    }

    public static class DraftViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvTitle, tvStatus;

        public DraftViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgRecipeThumb);
            tvTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvStatus = itemView.findViewById(R.id.tvRecipeStatus);
        }
    }
}