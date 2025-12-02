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
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());

        if (user.getPhotoUrl() != null) {
            Glide.with(context).load(user.getPhotoUrl()).circleCrop().into(holder.imgAvatar);
        }

        if (user.isBlocked()) {
            holder.tvBlocked.setVisibility(View.VISIBLE);
            holder.btnBlock.setText("Buka Blokir");
            holder.btnBlock.setTextColor(Color.BLACK);
        } else {
            holder.tvBlocked.setVisibility(View.GONE);
            holder.btnBlock.setText("Blokir");
            holder.btnBlock.setTextColor(Color.RED);
        }

        holder.btnBlock.setOnClickListener(v -> listener.onBlockClick(user));
    }

    @Override
    public int getItemCount() { return userList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvName, tvEmail, tvBlocked;
        Button btnBlock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_user_avatar);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
            tvBlocked = itemView.findViewById(R.id.tv_status_blocked);
            btnBlock = itemView.findViewById(R.id.btn_toggle_block);
        }
    }
}