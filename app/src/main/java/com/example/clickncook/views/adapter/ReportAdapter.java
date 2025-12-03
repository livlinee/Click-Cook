package com.example.clickncook.views.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clickncook.R;
import com.example.clickncook.models.Report;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private List<Report> reportList;
    private OnReportInteractListener listener;
    private FirebaseFirestore db;

    public interface OnReportInteractListener {
        void onClick(Report report);
        void onLongClick(Report report);
    }

    public ReportAdapter(List<Report> reportList, OnReportInteractListener listener) {
        this.reportList = reportList;
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance();
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

        holder.tvReporter.setText("Pelapor: Memuat...");
        db.collection("users").document(report.getReporterUserId())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String name = snapshot.getString("name");
                        holder.tvReporter.setText("Pelapor: " + (name != null ? name : "Tanpa Nama"));
                    } else {
                        holder.tvReporter.setText("Pelapor: User Tidak Ditemukan");
                    }
                })
                .addOnFailureListener(e -> holder.tvReporter.setText("Pelapor: Error"));

        holder.tvReported.setText("Konten: Memuat...");

        String collectionName = "recipe".equals(report.getContentType()) ? "recipes" : "reviews";

        db.collection(collectionName).document(report.getReportedContentId())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        if ("recipe".equals(report.getContentType())) {
                            String title = snapshot.getString("title");
                            holder.tvReported.setText("Resep: " + title);
                        } else {
                            String comment = snapshot.getString("comment");
                            if (comment != null && comment.length() > 30) {
                                comment = comment.substring(0, 30) + "...";
                            }
                            holder.tvReported.setText("Ulasan: " + (comment != null ? comment : "-"));
                        }
                    } else {
                        holder.tvReported.setText("Konten: Sudah Dihapus");
                    }
                });

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