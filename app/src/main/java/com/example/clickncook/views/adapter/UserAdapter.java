package com.example.clickncook.views.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.clickncook.R;
import com.example.clickncook.models.User;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;
    private OnBlockClickListener listener;

    public interface OnBlockClickListener {
        void onBlockClick(User user);
    }

    public UserAdapter(Context context, List<User> userList, OnBlockClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());

        if (user.getPhotoUrl() != null) {
            Glide.with(context)
                    .load(user.getPhotoUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person_placeholder)
                    .into(holder.imgAvatar);
        }

        if (user.isBlocked()) {
            holder.tvStatus.setText("Diblokir");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_gray);
            holder.btnAction.setText("Buka Blokir");
            holder.btnAction.setTextColor(Color.BLACK);
        } else {
            holder.tvStatus.setText("Aktif");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_green);
            holder.btnAction.setText("Blokir");
            holder.btnAction.setTextColor(context.getResources().getColor(R.color.primary_orange));
        }

        holder.btnAction.setOnClickListener(v -> listener.onBlockClick(user));
    }

    @Override
    public int getItemCount() { return userList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvName, tvEmail, tvStatus;
        Button btnAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgUserAvatar);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvStatus = itemView.findViewById(R.id.tvStatusTag);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}