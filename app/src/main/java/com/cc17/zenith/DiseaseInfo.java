package com.cc17.zenith;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;


public class DiseaseInfo extends Fragment {
    private Disease disease;
    private TextView tvInfoContent;
    private MaterialButton btnDesc, btnSymp, btnMeds;

    public void DiseaseDetailFragment() {
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

        setupStatBox(view.findViewById(R.id.statActive), "Active Cases", String.valueOf(disease.getActiveCases()));
        setupStatBox(view.findViewById(R.id.statNew), "New Cases", "+" + disease.getNewCases());
        setupStatBox(view.findViewById(R.id.statTotal), "Total Cases", String.valueOf(disease.getTotalCases()));
        setupStatBox(view.findViewById(R.id.statRate), "Fatality Rate", disease.getFatalityRate());

        // 3. Draw Graphs (These require SimpleLineGraph and SimpleBarGraph classes)
        SimpleLineGraph lineGraph = view.findViewById(R.id.graphTrend);
        if (lineGraph != null) {
            lineGraph.setData(disease.getTrendData());
        }

        SimpleBarGraph barGraph = view.findViewById(R.id.graphSeverity);
        if (barGraph != null) {
            barGraph.setData(disease.getSeverityData());
        }

        // 4. Setup Tabs
        tvInfoContent = view.findViewById(R.id.tvInfoContent);
        btnDesc = view.findViewById(R.id.btnDesc);
        btnSymp = view.findViewById(R.id.btnSymp);
        btnMeds = view.findViewById(R.id.btnMeds);

        // Default content (Description)
        updateTabSelection(btnDesc, disease.getDescription());

        // Tab Click Listeners
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

    private void updateTabSelection(MaterialButton selected, String content) {
        tvInfoContent.setText(content);

        int coral = ContextCompat.getColor(requireContext(), R.color.coral);
        int moonstone = ContextCompat.getColor(requireContext(), R.color.moonstone);
        int lightBlue = Color.parseColor("#F0F9F7");

        // Reset all buttons to "unselected" style
        btnDesc.setBackgroundColor(lightBlue);
        btnDesc.setTextColor(moonstone);
        btnSymp.setBackgroundColor(lightBlue);
        btnSymp.setTextColor(moonstone);
        btnMeds.setBackgroundColor(lightBlue);
        btnMeds.setTextColor(moonstone);

        // Highlight selected button
        selected.setBackgroundColor(coral);
        selected.setTextColor(Color.WHITE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_disease_info, container, false);
    }
}