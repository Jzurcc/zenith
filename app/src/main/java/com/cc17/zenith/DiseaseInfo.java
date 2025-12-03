package com.cc17.zenith;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DiseaseInfo extends Fragment {
    private Disease disease;
    private TextView tvInfoContent;
    private MaterialButton btnDesc, btnSymp, btnMeds;

    public DiseaseInfo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_disease_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setTexts("Disease Trends", "Track Diseases");

        if (getArguments() != null) {
            disease = (Disease) getArguments().getSerializable("disease_data");
        }

        if (disease == null) return;

        TextView title = view.findViewById(R.id.tvDetailTitle);
        TextView date = view.findViewById(R.id.tvDetailDate);

        title.setText(disease.getName());
        date.setText(disease.getDate());

        // Date Picker Logic
        date.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        // Format the date to match the existing style (e.g. Oct 31)
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year1, monthOfYear, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
                        date.setText(sdf.format(selectedDate.getTime()));
                    }, year, month, day);
            datePickerDialog.show();
        });

        setupStatBox(view.findViewById(R.id.statActive), "Active Cases", String.valueOf(disease.getActiveCases()));
        setupStatBox(view.findViewById(R.id.statNew), "New Cases", "+" + disease.getNewCases());
        setupStatBox(view.findViewById(R.id.statTotal), "Total Cases", String.valueOf(disease.getTotalCases()));
        setupStatBox(view.findViewById(R.id.statRate), "Fatality Rate", disease.getFatalityRate());

        // 3. Draw Graphs
        SimpleLineGraph lineGraph = view.findViewById(R.id.graphTrend);
        if (lineGraph != null) {
            lineGraph.setData(disease.getTrendData());
        }

        SimpleBarGraph barGraph = view.findViewById(R.id.graphSeverity);
        LinearLayout legendContainer = view.findViewById(R.id.layoutSeverityLegend);

        if (barGraph != null && disease.getSeverityData() != null) {
            barGraph.setData(disease.getSeverityData());

            // Populate Legend
            setupSeverityLegend(legendContainer, disease.getSeverityData());
        }

        // 4. Setup Tabs
        tvInfoContent = view.findViewById(R.id.tvInfoContent);
        btnDesc = view.findViewById(R.id.btnDesc);
        btnSymp = view.findViewById(R.id.btnSymp);
        btnMeds = view.findViewById(R.id.btnMeds);

        // Default content
        updateTabSelection(btnDesc, disease.getDescription());

        btnDesc.setOnClickListener(v -> updateTabSelection(btnDesc, disease.getDescription()));
        btnSymp.setOnClickListener(v -> updateTabSelection(btnSymp, disease.getSymptoms()));
        btnMeds.setOnClickListener(v -> updateTabSelection(btnMeds, disease.getMedication()));
    }

    private void setupStatBox(View container, String label, String value) {
        if (container != null) {
            TextView lbl = container.findViewById(R.id.tvLabel);
            TextView val = container.findViewById(R.id.tvValue);
            lbl.setText(label);
            val.setText(value);
        }
    }

    private void setupSeverityLegend(LinearLayout container, List<Integer> data) {
        if (container == null || data == null || data.isEmpty()) return;
        container.removeAllViews();

        int total = 0;
        for (int count : data) total += count;
        if (total == 0) total = 1; // Prevent divide by zero

        // Defined categories matching the colors in SimpleBarGraph
        String[] labels = {"Mild Cases", "Moderate Cases", "Severe Cases"};
        String[] colors = {"#00BCD4", "#FF7043", "#006064"}; // Teal, Orange, Dark Teal

        for (int i = 0; i < data.size(); i++) {
            if (i >= labels.length) break;

            int count = data.get(i);
            int percent = (count * 100) / total;

            addLegendItem(container, labels[i], percent + "% (" + count + ")", colors[i]);
        }
    }

    private void addLegendItem(LinearLayout container, String name, String value, String colorHex) {
        if (getContext() == null) return;

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_legend_row, container, false);

        TextView tvName = view.findViewById(R.id.tvDiseaseName);
        TextView tvValue = view.findViewById(R.id.tvPercent);
        View dot = view.findViewById(R.id.viewDot);

        tvName.setText(name);
        tvValue.setText(value);
        dot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(colorHex)));

        container.addView(view);
    }

    private void updateTabSelection(MaterialButton selected, String content) {
        tvInfoContent.setText(content);

        int coral = ContextCompat.getColor(requireContext(), R.color.coral);
        int moonstone = ContextCompat.getColor(requireContext(), R.color.moonstone);
        int lightBlue = Color.parseColor("#D1F1F6");

        btnDesc.setBackgroundColor(lightBlue);
        btnDesc.setTextColor(moonstone);
        btnSymp.setBackgroundColor(lightBlue);
        btnSymp.setTextColor(moonstone);
        btnMeds.setBackgroundColor(lightBlue);
        btnMeds.setTextColor(moonstone);

        selected.setBackgroundColor(coral);
        selected.setTextColor(Color.WHITE);
    }
}