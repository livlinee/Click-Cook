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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_report_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = reportList.get(position);

        holder.tvViolationType.setText("Pelanggaran: " + report.getReason());
        holder.tvReporter.setText("Pelapor ID: " + report.getReporterUserId());
        holder.tvReported.setText("Konten Terlapor ID: " + report.getReportedContentId());

        holder.itemView.setOnClickListener(v -> listener.onClick(report));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(report);
            return true;
        });
    }

    @Override
    public int getItemCount() { return reportList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvViolationType, tvReporter, tvReported;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvViolationType = itemView.findViewById(R.id.tvViolationType);
            tvReporter = itemView.findViewById(R.id.tvUsers);
            tvReported = itemView.findViewById(R.id.tvReportedUser);
        }
    }
}