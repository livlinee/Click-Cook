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
    private boolean isStep;

    public IngredientAdapter(Context context, List<String> listData, boolean isStep) {
        this.context = context;
        this.listData = listData;
        this.isStep = isStep;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isStep) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_step_card, parent, false);
            return new ViewHolder(view, true);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient_list, parent, false);
            return new ViewHolder(view, false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String data = listData.get(position);

        if (isStep) {
            holder.tvStepNumber.setText(String.valueOf(position + 1));
            holder.tvStepInstruction.setText(data);
        } else {
            holder.tvIngredientName.setText(data);
            holder.tvIngredientAmount.setText("");
        }
    }

    @Override
    public int getItemCount() { return listData.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStepNumber, tvStepInstruction, tvStepTime;
        TextView tvIngredientName, tvIngredientAmount;

        public ViewHolder(@NonNull View itemView, boolean isStepLayout) {
            super(itemView);
            if (isStepLayout) {
                tvStepNumber = itemView.findViewById(R.id.tvStepNumber);
                tvStepInstruction = itemView.findViewById(R.id.tvStepInstruction);
            } else {
                tvIngredientName = itemView.findViewById(R.id.tvIngredientName);
                tvIngredientAmount = itemView.findViewById(R.id.tvIngredientAmount);
            }
        }
    }
}