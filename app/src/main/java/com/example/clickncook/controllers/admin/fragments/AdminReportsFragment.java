package com.example.clickncook.controllers.admin.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clickncook.R;
import com.example.clickncook.models.Report;
import com.example.clickncook.views.adapter.ReportAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import java.util.ArrayList;
import java.util.List;

public class AdminReportsFragment extends Fragment {

    private FirebaseFirestore db;
    private ReportAdapter adapter;
    private List<Report> reportList;
    private String currentStatus = "Pending";

    private TextView tvPending, tvResolved;
    private View indicatorPending, indicatorResolved;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_admin_reports, container, false);

        View staticNav = view.findViewById(R.id.adminBottomNavContainer);
        if (staticNav != null) staticNav.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();

        tvPending = view.findViewById(R.id.tvPending);
        tvResolved = view.findViewById(R.id.tvResolved);
        indicatorPending = view.findViewById(R.id.indicatorPending);
        indicatorResolved = view.findViewById(R.id.indicatorResolved);

        RecyclerView recyclerView = view.findViewById(R.id.rvReports);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reportList = new ArrayList<>();
        adapter = new ReportAdapter(reportList, new ReportAdapter.OnReportInteractListener() {
            @Override
            public void onClick(Report report) {
                if ("recipe".equals(report.getContentType())) {
                    Toast.makeText(getContext(), "Cek Resep ID: " + report.getReportedContentId(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(Report report) {
                if ("Pending".equals(currentStatus)) showActionDialog(report);
            }
        });
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.tabPending).setOnClickListener(v -> {
            updateTabUI(true);
            currentStatus = "Pending";
            loadReports();
        });

        view.findViewById(R.id.tabResolved).setOnClickListener(v -> {
            updateTabUI(false);
            currentStatus = "Resolved";
            loadResolvedReports();
        });

        loadReports();
        return view;
    }

    private void updateTabUI(boolean isPendingActive) {
        int orange = ContextCompat.getColor(getContext(), R.color.primary_orange);
        int gray = ContextCompat.getColor(getContext(), R.color.gray_text);
        int grayStroke = ContextCompat.getColor(getContext(), R.color.gray_stroke);

        if (isPendingActive) {
            tvPending.setTextColor(orange);
            indicatorPending.setBackgroundColor(orange);

            tvResolved.setTextColor(gray);
            indicatorResolved.setBackgroundColor(grayStroke);
        } else {
            tvPending.setTextColor(gray);
            indicatorPending.setBackgroundColor(grayStroke);

            tvResolved.setTextColor(orange);
            indicatorResolved.setBackgroundColor(orange);
        }
    }

    private void loadReports() {
        db.collection("reports")
                .whereEqualTo("status", "Pending")
                .get()
                .addOnSuccessListener(snapshots -> {
                    reportList.clear();
                    for (DocumentSnapshot doc : snapshots) {
                        Report r = doc.toObject(Report.class);
                        if (r != null) {
                            r.setId(doc.getId());
                            reportList.add(r);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void loadResolvedReports() {
        db.collection("reports")
                .whereNotEqualTo("status", "Pending")
                .get()
                .addOnSuccessListener(snapshots -> {
                    reportList.clear();
                    for (DocumentSnapshot doc : snapshots) {
                        Report r = doc.toObject(Report.class);
                        if (r != null) {
                            r.setId(doc.getId());
                            reportList.add(r);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void showActionDialog(Report report) {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(R.layout.dialog_report_action);

        dialog.findViewById(R.id.btnDelete).setOnClickListener(v -> {
            resolveReport(report, "Resolved_Deleted", true);
            dialog.dismiss();
        });

        dialog.findViewById(R.id.btnReject).setOnClickListener(v -> {
            resolveReport(report, "Rejected", false);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void resolveReport(Report report, String newStatus, boolean deleteContent) {
        WriteBatch batch = db.batch();
        DocumentReference reportRef = db.collection("reports").document(report.getId());
        batch.update(reportRef, "status", newStatus);

        if (deleteContent) {
            String collection = report.getContentType().equals("recipe") ? "recipes" : "reviews";
            DocumentReference contentRef = db.collection(collection).document(report.getReportedContentId());
            batch.delete(contentRef);
        }

        batch.commit().addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Laporan Diproses", Toast.LENGTH_SHORT).show();
            if(currentStatus.equals("Pending")) loadReports();
            else loadResolvedReports();
        });
    }
}