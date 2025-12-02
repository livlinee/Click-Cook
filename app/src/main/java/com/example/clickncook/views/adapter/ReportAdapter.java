package com.example.clickncook.views.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clickncook.R;
import com.example.clickncook.models.Report;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private List<Report> reportList;
    private OnReportInteractListener listener;

    public interface OnReportInteractListener {
        void onClick(Report report);
        void onLongClick(Report report);
    }

    public ReportAdapter(List<Report> reportList, OnReportInteractListener listener) {
        this.reportList = reportList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.tvReason.setText(report.getReason());
        holder.tvReporter.setText("Pelapor ID: " + report.getReporterUserId());
        holder.tvReported.setText("Konten ID: " + report.getReportedContentId());

        if (report.getStatus().equals("Pending")) {
            holder.tvResolved.setVisibility(View.GONE);
        } else {
            holder.tvResolved.setVisibility(View.VISIBLE);
            holder.tvResolved.setText(report.getStatus());
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(report));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(report);
            return true;
        });
    }

    @Override
    public int getItemCount() { return reportList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReason, tvReporter, tvReported, tvResolved;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReason = itemView.findViewById(R.id.tv_reason);
            tvReporter = itemView.findViewById(R.id.tv_reporter);
            tvReported = itemView.findViewById(R.id.tv_reported_content);
            tvResolved = itemView.findViewById(R.id.tv_status_resolved);
        }
    }
}