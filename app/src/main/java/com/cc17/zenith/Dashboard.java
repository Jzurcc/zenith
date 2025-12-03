package com.cc17.zenith;

import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class Dashboard extends Fragment {

    public Dashboard() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            sharedViewModel.setTexts("Dashboard", "Analytics Overview");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Setup Date Pickers
        setupDatePickers(view);

        // 2. Setup Top Stats Cards with Custom Icons
        setupStatCard(view.findViewById(R.id.cardInpatients), "Inpatients", "1,034", "+11.6%", R.color.moonstone, R.drawable.profile2);
        setupStatCard(view.findViewById(R.id.cardOutpatients), "Outpatients", "389", "-2.3%", R.color.coral, R.drawable.profile);
        setupStatCard(view.findViewById(R.id.cardDiagnoses), "Diagnoses", "58", "+5.6%", R.color.coral, R.drawable.ehr);
        // Renamed from Appointments to Diseases, using Virus icon
        setupStatCard(view.findViewById(R.id.cardAppointments), "Diseases", "13", "+7.2%", R.color.moonstone, R.drawable.virus);

        // 3. Setup Graphs with SAMPLE DATA
        SimpleLineGraph graphActive = view.findViewById(R.id.graphActiveCases);
        if (graphActive != null) {
            graphActive.setData(Arrays.asList(20, 35, 15, 45, 30, 60, 40, 55, 35, 25));
        }

        SimpleBarGraph graphTop = view.findViewById(R.id.graphTopDiseases);
        if (graphTop != null) {
            graphTop.setData(Arrays.asList(81, 63, 52, 47));
        }

        SimpleBarGraph graphDistribution = view.findViewById(R.id.graphPatientDistribution);
        if (graphDistribution != null) {
            graphDistribution.setData(Arrays.asList(45, 30, 25));
        }

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
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_layout, new diseasetrends())
                        .addToBackStack(null)
                        .commit();
            });
        }
    }

    private void setupDatePickers(View view) {
        TextView tvStart = view.findViewById(R.id.tvActiveDateStart);
        TextView tvEnd = view.findViewById(R.id.tvActiveDateEnd);
        TextView tvTop = view.findViewById(R.id.tvTopDiseasesDate);

        setupDateListener(tvStart);
        setupDateListener(tvEnd);
        setupDateListener(tvTop);
    }

    private void setupDateListener(TextView textView) {
        if (textView == null) return;
        textView.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        // Format selected date to "MMM yyyy" (e.g., Oct 2025)
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year1, monthOfYear, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                        textView.setText(sdf.format(selectedDate.getTime()));
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

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