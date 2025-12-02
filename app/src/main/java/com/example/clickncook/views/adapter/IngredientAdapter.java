package com.example.clickncook.views.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clickncook.R;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private Context context;
    private List<String> listData;
    private boolean isOrdered;

    public IngredientAdapter(Context context, List<String> listData, boolean isOrdered) {
        this.context = context;
        this.listData = listData;
        this.isOrdered = isOrdered;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = listData.get(position);
        holder.tvContent.setText(text);

        if (isOrdered) {
            holder.tvBullet.setText((position + 1) + ".");
        } else {
            holder.tvBullet.setText("•");
        }
    }

    @Override
    public int getItemCount() { return listData.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBullet, tvContent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBullet = itemView.findViewById(R.id.tv_bullet);
            tvContent = itemView.findViewById(R.id.tv_content);
        }
    }
}