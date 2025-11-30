package com.cc17.zenith;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class diseasetrends extends Fragment {

    // UI Components
    private DiseaseAdapter diseaseAdapter;
    private RecyclerView rvList;
    private SearchView searchView;
    private MaterialButton btnComm, btnNonComm, btnChronic, btnAcute;

    // State Variables
    private String currentSearchText = "";
    private String selectedCategory = null; // Null means "Show All"
    private List<Disease> allDiseases;

    public diseasetrends() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout (Make sure your XML file is named fragment_disease_trends)
        return inflater.inflate(R.layout.fragment_diseasetrends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Existing SharedViewModel Logic (Preserved)
        try {
            SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            sharedViewModel.setTexts("Disease Trends", "Track Diseases");
        } catch (Exception e) {
            // Safety check in case ViewModel isn't ready
        }

        // 2. Initialize Mock Data (This replaces your hardcoded buttons)
        initializeData();

        // 3. Initialize Views
        rvList = view.findViewById(R.id.rvDiseaseList);
        searchView = view.findViewById(R.id.searchView);

        // Note: These IDs must match what is in your XML file
        btnComm = view.findViewById(R.id.btnCatCommunicable);
        btnNonComm = view.findViewById(R.id.btnCatNonCommunicable);
        btnChronic = view.findViewById(R.id.btnCatChronic);
        btnAcute = view.findViewById(R.id.btnCatAcute);

        // 4. Setup RecyclerView
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        diseaseAdapter = new DiseaseAdapter(allDiseases);
        rvList.setAdapter(diseaseAdapter);

        // 5. Setup Listeners
        setupSearch();
        setupCategories();
    }

    private void initializeData() {
        allDiseases = new ArrayList<>();
        allDiseases.add(new Disease("1", "Dengue", "Communicable", 32, "Oct 31"));
        allDiseases.add(new Disease("2", "Influenza", "Communicable", 17, "Oct 31"));
        allDiseases.add(new Disease("3", "Tuberculosis", "Communicable", 32, "Oct 31"));
        allDiseases.add(new Disease("4", "Diabetes", "Non-communicable", 120, "Oct 30"));
        allDiseases.add(new Disease("5", "Hypertension", "Non-communicable", 98, "Oct 30"));
        allDiseases.add(new Disease("6", "Asthma", "Chronic", 45, "Oct 29"));
        allDiseases.add(new Disease("7", "Bronchitis", "Acute", 12, "Oct 29"));
    }

    private void setupSearch() {
        // UX: Click whole bar to expand
        searchView.setOnClickListener(v -> searchView.onActionViewExpanded());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchText = newText != null ? newText : "";
                applyFilters();
                return true;
            }
        });
    }

    private void setupCategories() {
        // Helper class to map buttons to their category strings
        class ButtonCategoryPair {
            MaterialButton btn;
            String category;
            ButtonCategoryPair(MaterialButton btn, String category) {
                this.btn = btn;
                this.category = category;
            }
        }

        List<ButtonCategoryPair> pairs = Arrays.asList(
                new ButtonCategoryPair(btnComm, "Communicable"),
                new ButtonCategoryPair(btnNonComm, "Non-communicable"),
                new ButtonCategoryPair(btnChronic, "Chronic"),
                new ButtonCategoryPair(btnAcute, "Acute")
        );

        for (ButtonCategoryPair pair : pairs) {
            pair.btn.setOnClickListener(v -> {
                // Toggle logic: if clicking already selected, turn it off.
                if (pair.category.equals(selectedCategory)) {
                    selectedCategory = null;
                    updateButtonStyles(null);
                } else {
                    selectedCategory = pair.category;
                    updateButtonStyles(pair.btn);
                }
                applyFilters();
            });
        }
    }

    private void applyFilters() {
        List<Disease> filteredList = new ArrayList<>();

        for (Disease disease : allDiseases) {
            boolean matchesSearch = disease.getName().toLowerCase().contains(currentSearchText.toLowerCase());
            boolean matchesCategory = selectedCategory == null || disease.getCategory().equalsIgnoreCase(selectedCategory);

            if (matchesSearch && matchesCategory) {
                filteredList.add(disease);
            }
        }
        diseaseAdapter.updateList(filteredList);
    }

    private void updateButtonStyles(MaterialButton selectedBtn) {
        List<MaterialButton> allBtns = Arrays.asList(btnComm, btnNonComm, btnChronic, btnAcute);

        int colorMoonstone = ContextCompat.getColor(requireContext(), R.color.moonstone);
        int colorCoral = ContextCompat.getColor(requireContext(), R.color.coral);

        for (MaterialButton btn : allBtns) {
            btn.setBackgroundColor(Color.TRANSPARENT);
            btn.setTextColor(colorMoonstone);
            btn.setStrokeWidth(3);
        }

        // Selected Filled Style
        if (selectedBtn != null) {
            selectedBtn.setBackgroundColor(colorCoral);
            selectedBtn.setTextColor(Color.WHITE);
            selectedBtn.setStrokeWidth(0);
        }
    }
}