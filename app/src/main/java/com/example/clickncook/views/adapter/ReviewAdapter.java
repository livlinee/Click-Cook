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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private Context context;
    private List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.tvUserName.setText(review.getUserName());
        holder.tvComment.setText(review.getComment());
        holder.ratingBar.setRating((float) review.getRating());

        holder.tvTime.setText("Baru saja");

        if (review.getUserPhotoUrl() != null) {
            Glide.with(context)
                    .load(review.getUserPhotoUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person_placeholder)
                    .into(holder.imgAvatar);
        }
    }

    @Override
    public int getItemCount() { return reviewList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvUserName, tvTime, tvComment;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTime = itemView.findViewById(R.id.tvTime);
            ratingBar = itemView.findViewById(R.id.rbReviewStars);
            tvComment = itemView.findViewById(R.id.tvCommentContent);
        }
    }
}