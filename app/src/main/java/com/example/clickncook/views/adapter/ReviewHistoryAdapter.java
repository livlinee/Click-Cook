package com.example.clickncook.views.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.clickncook.R;
import com.example.clickncook.models.Review;
import java.util.List;

public class ReviewHistoryAdapter extends RecyclerView.Adapter<ReviewHistoryAdapter.ViewHolder> {

    private Context context;
    private List<Review> reviewList;
    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onDeleteClick(Review review);
    }

    public ReviewHistoryAdapter(Context context, List<Review> reviewList, OnActionClickListener listener) {
        this.context = context;
        this.reviewList = reviewList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.tvRecipeName.setText(review.getRecipeTitle());
        holder.tvComment.setText(review.getComment());
        holder.ratingBar.setRating((float) review.getRating());

        if (review.getRecipeImageUrl() != null) {
            Glide.with(context).load(review.getRecipeImageUrl()).into(holder.imgRecipe);
        }

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(review));
    }

    @Override
    public int getItemCount() { return reviewList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipe, btnDelete;
        TextView tvRecipeName, tvComment;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipe = itemView.findViewById(R.id.imgRecipe);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeTitle);
            tvComment = itemView.findViewById(R.id.tvComment);
            ratingBar = itemView.findViewById(R.id.rbRating);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}