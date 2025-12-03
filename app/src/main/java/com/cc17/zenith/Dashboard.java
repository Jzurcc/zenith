package com.cc17.zenith;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;

public class Dashboard extends Fragment {

    public Dashboard() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Make sure your XML file is named fragment_dashboard.xml and contains the graphs/cards
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Update the Main Activity Toolbar Title
        try {
            SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            sharedViewModel.setTexts("Dashboard", "Analytics Overview");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Setup Top Stats Cards (Populating data)
        // Note: We use the IDs from the <include> tags in your XML
        setupStatCard(view.findViewById(R.id.cardInpatients), "Inpatients", "1,034", "+11.6%", R.color.moonstone, android.R.drawable.ic_menu_view);
        setupStatCard(view.findViewById(R.id.cardOutpatients), "Outpatients", "389", "-2.3%", R.color.coral, android.R.drawable.ic_menu_rotate);
        setupStatCard(view.findViewById(R.id.cardDiagnoses), "Diagnoses", "58", "+5.6%", R.color.coral, android.R.drawable.ic_menu_edit);
        setupStatCard(view.findViewById(R.id.cardAppointments), "Appointments", "13", "+7.2%", R.color.moonstone, android.R.drawable.ic_menu_today);

        // 3. Setup Graphs with SAMPLE DATA

        // A. Active Cases Line Graph
        SimpleLineGraph graphActive = view.findViewById(R.id.graphActiveCases);
        if (graphActive != null) {
            graphActive.setData(Arrays.asList(20, 35, 15, 45, 30, 60, 40, 55, 35, 25));
        }

        // B. Top Diseases Bar Graph
        SimpleBarGraph graphTop = view.findViewById(R.id.graphTopDiseases);
        if (graphTop != null) {
            graphTop.setData(Arrays.asList(81, 63, 52, 47));
        }

        // C. Patient Distribution Bar Graph (NEW)
        SimpleBarGraph graphDistribution = view.findViewById(R.id.graphPatientDistribution);
        if (graphDistribution != null) {
            // Sample data representing ER, Inpatient, Outpatient distribution
            graphDistribution.setData(Arrays.asList(45, 30, 25));
        }

        // D. Admissions Line Graph (Bottom)
        SimpleLineGraph graphAdmissions = view.findViewById(R.id.graphAdmissions);
        if (graphAdmissions != null) {
            graphAdmissions.setData(Arrays.asList(10, 20, 15, 30, 25, 40, 35));
        }

        // 4. Populate Legend for Bar Graph
        LinearLayout legendContainer = view.findViewById(R.id.layoutLegend);
        if (legendContainer != null) {
            legendContainer.removeAllViews();
            addLegendItem(legendContainer, "Influenza", "81.57%", "#FF7043");
            addLegendItem(legendContainer, "Dengue", "63.25%", "#00BCD4");
            addLegendItem(legendContainer, "Tuberculosis", "52.95%", "#FFAB91");
            addLegendItem(legendContainer, "STIs", "47.29%", "#006064");
        }

        // 5. "View More" Button Logic
        MaterialButton btnViewMore = view.findViewById(R.id.btnViewMore);
        if (btnViewMore != null) {
            btnViewMore.setOnClickListener(v -> {
                // Navigate to the detailed Disease Trends Fragment
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_layout, new diseasetrends())
                        .addToBackStack(null)
                        .commit();
            });
        }
    }

    // Helper to populate the small stat cards dynamically
    private void setupStatCard(View cardView, String label, String count, String percent, int colorResId, int iconResId) {
        if (cardView == null) return;

        MaterialCardView card = (MaterialCardView) cardView;
        int color = ContextCompat.getColor(requireContext(), colorResId);
        card.setCardBackgroundColor(color);

        TextView tvLabel = card.findViewById(R.id.tvLabel);
        TextView tvCount = card.findViewById(R.id.tvCount);
        TextView tvPercent = card.findViewById(R.id.tvPercentage);
        ImageView ivIcon = card.findViewById(R.id.ivIcon);

        if (tvLabel != null) tvLabel.setText(label);
        if (tvCount != null) tvCount.setText(count);
        if (tvPercent != null) tvPercent.setText(percent);
        if (ivIcon != null) ivIcon.setImageResource(iconResId);
    }

    // Helper to add rows to the legend
    private void addLegendItem(LinearLayout container, String name, String percent, String colorHex) {
        if (getContext() == null) return;

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_legend_row, container, false);

        TextView tvName = view.findViewById(R.id.tvDiseaseName);
        TextView tvPercent = view.findViewById(R.id.tvPercent);
        View dot = view.findViewById(R.id.viewDot);

        if (tvName != null) tvName.setText(name);
        if (tvPercent != null) tvPercent.setText(percent);
        if (dot != null) {
            dot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(colorHex)));
        }

        container.addView(view);
    }
}