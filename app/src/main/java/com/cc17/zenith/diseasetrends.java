package com.cc17.zenith;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class diseasetrends extends Fragment implements DiseaseAdapter.OnDiseaseClickListener {

    // UI Components
    private DiseaseAdapter diseaseAdapter;
    private RecyclerView rvList;
    private SearchView searchView;
    private ImageButton btnFilter;
    private MaterialButton btnComm, btnNonComm, btnChronic, btnAcute;

    // State Variables
    private String currentSearchText = "";
    private String selectedCategory = null; // Null means "Show All"
    private List<Disease> allDiseases;
    private Comparator<Disease> currentComparator = Comparator.comparingInt(Disease::getActiveCases).reversed();

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
        btnFilter = view.findViewById(R.id.btnFilter);

        // Note: These IDs must match what is in your XML file
        btnComm = view.findViewById(R.id.btnCatCommunicable);
        btnNonComm = view.findViewById(R.id.btnCatNonCommunicable);
        btnChronic = view.findViewById(R.id.btnCatChronic);
        btnAcute = view.findViewById(R.id.btnCatAcute);

        // 4. Setup RecyclerView
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        diseaseAdapter = new DiseaseAdapter(allDiseases, this);
        rvList.setAdapter(diseaseAdapter);

        applyFilters();

        // 5. Setup Listeners
        setupSearch();
        setupCategories();
        setupFilterMenu();
    }

    private void initializeData() {
        allDiseases = new ArrayList<>();

        String todayStr = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date());

        // --- COMMUNICABLE ---
        allDiseases.add(new Disease(
                "1", "Dengue", "Communicable", 32, "Sept 27",
                12, 612, "2%",
                "Dengue is a mosquito-borne viral disease occurring in tropical and subtropical areas.",
                "High fever, headache, vomiting, muscle and joint pains, skin rash.",
                "Pain relievers (Acetaminophen), fluids, and rest. Avoid aspirin.",
                Arrays.asList(10, 25, 15, 40, 30, 60, 32),
                Arrays.asList(20, 10, 2)
        ));

        allDiseases.add(new Disease(
                "2", "Influenza", "Communicable", 600, todayStr,
                45, 1200, "0.5%",
                "Influenza is a viral infection that attacks your respiratory system.",
                "Fever, chills, muscle aches, cough, congestion, runny nose.",
                "Antiviral drugs (Oseltamivir), bed rest, plenty of fluids.",
                Arrays.asList(500, 520, 480, 600, 580, 610, 600),
                Arrays.asList(500, 90, 10)
        ));

        allDiseases.add(new Disease(
                "3", "Tuberculosis", "Communicable", 600, todayStr,
                12, 612, "6%",
                "Tuberculosis, also called TB, is a serious illness that mainly affects the lungs. The germs that cause tuberculosis are a type of bacteria.\n" +
                        "\n" +
                        "Tuberculosis can spread when a person with the illness coughs, sneezes or sings. This can put tiny droplets with the germs into the air. Another person can then breathe in the droplets, and the germs enter the lungs.\n" +
                        "\n" +
                        "Tuberculosis spreads easily where people gather in crowds or where people live in crowded conditions. People with HIV/AIDS and other people with weakened immune systems have a higher risk of catching tuberculosis than people with typical immune systems.",
                "Common symptoms of TB often include:\n" +
                        "\n" +
                        "- Persistent cough (≥2 weeks)\n" +
                        "- Sputum production or blood-streaked sputum (hemoptysis)\n" +
                        "- Low-grade fever\n" +
                        "- Night sweats\n" +
                        "- Fatigue and weakness\n" +
                        "- Unintentional weight loss\n" +
                        "- Loss of appetite\n" +
                        "- Chest pain\n" +
                        "- Swollen lymph nodes (extrapulmonary TB)\n" +
                        "- Back or abdominal pain (extrapulmonary TB)",
                "Tuberculosis is treated with a combination of antibiotics over at least six months. \n" +
                        "\n" +
                        "The standard regimen, called RIPE therapy, includes Rifampicin, Isoniazid, Pyrazinamide, and Ethambutol during the first two months (intensive phase), followed by Rifampicin and Isoniazid for the next four months (continuation phase).  Adherence to the full course is essential to prevent relapse and resistance.\n" +
                        "\n" +
                        "Directly Observed Therapy (DOT) is often used to ensure patients complete treatment, supported by good nutrition, rest, and monitoring for side effects like liver or vision problems.",
                Arrays.asList(300, 320, 350, 400, 380, 500, 600),
                Arrays.asList(400, 150, 50)
        ));

        allDiseases.add(new Disease(
                "4", "COVID-19", "Communicable", 150, "Nov 30",
                25, 2300, "1.2%",
                "A disease caused by SARS-CoV-2 that can trigger what doctors call a respiratory tract infection.",
                "Fever, dry cough, tiredness, loss of taste or smell.",
                "Supportive care, antivirals (Paxlovid) for high risk patients.",
                Arrays.asList(50, 80, 120, 100, 140, 130, 150),
                Arrays.asList(100, 40, 10)
        ));

        allDiseases.add(new Disease(
                "5", "Pneumonia", "Communicable", 85, todayStr,
                10, 450, "3.5%",
                "Infection that inflames air sacs in one or both lungs, which may fill with fluid.",
                "Chest pain when breathing, cough with phlegm, fatigue, fever.",
                "Antibiotics (if bacterial), cough medicine, fever reducers.",
                Arrays.asList(60, 65, 70, 75, 80, 82, 85),
                Arrays.asList(50, 25, 10)
        ));


        // --- NON-COMMUNICABLE ---
        allDiseases.add(new Disease(
                "6", "Diabetes Type 2", "Non-communicable", 120, "Nov 23",
                2, 5000, "0.1%",
                "A chronic condition that affects the way the body processes blood sugar (glucose).",
                "Increased thirst, frequent urination, hunger, fatigue, blurred vision.",
                "Metformin, insulin therapy, diet, and exercise.",
                Arrays.asList(115, 116, 118, 118, 119, 119, 120),
                Arrays.asList(80, 30, 10)
        ));

        allDiseases.add(new Disease(
                "7", "Hypertension", "Non-communicable", 98, todayStr,
                5, 4200, "0.2%",
                "A condition in which the force of the blood against the artery walls is too high.",
                "Often no symptoms. Headaches, shortness of breath, nosebleeds in severe cases.",
                "ACE inhibitors, beta-blockers, diet changes (low sodium).",
                Arrays.asList(90, 92, 91, 94, 95, 96, 98),
                Arrays.asList(60, 30, 8)
        ));


        // --- CHRONIC ---
        allDiseases.add(new Disease(
                "8", "Asthma", "Chronic", 45, todayStr,
                1, 800, "0.05%",
                "A condition in which your airways narrow and swell and may produce extra mucus.",
                "Shortness of breath, chest tightness or pain, wheezing.",
                "Inhalers (Albuterol), corticosteroids.",
                Arrays.asList(40, 38, 42, 45, 41, 43, 45),
                Arrays.asList(30, 12, 3)
        ));

        allDiseases.add(new Disease(
                "9", "Arthritis", "Chronic", 210, todayStr,
                3, 1500, "0%",
                "Swelling and tenderness of one or more joints.",
                "Joint pain, stiffness, swelling, redness, decreased range of motion.",
                "Painkillers, NSAIDs, physical therapy.",
                Arrays.asList(205, 206, 208, 208, 209, 210, 210),
                Arrays.asList(150, 50, 10)
        ));


        // --- ACUTE ---
        allDiseases.add(new Disease(
                "10", "Acute Bronchitis", "Acute", 12, todayStr,
                4, 150, "0%",
                "Inflammation of the lining of your bronchial tubes, which carry air to and from your lungs.",
                "Cough, production of mucus, fatigue, slight fever and chills.",
                "Rest, fluids, cough suppressants, humidifier.",
                Arrays.asList(5, 8, 15, 18, 14, 10, 12),
                Arrays.asList(8, 3, 1)
        ));
    }

    private void setupFilterMenu() {
        btnFilter.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);
            popup.getMenuInflater().inflate(R.menu.disease_filter_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.filter_name_asc) {
                    currentComparator = Comparator.comparing(Disease::getName);
                }
                else if (id == R.id.filter_name_desc) {
                    currentComparator = Comparator.comparing(Disease::getName).reversed();
                }
                else if (id == R.id.filter_cases_high) {
                    currentComparator = Comparator.comparingInt(Disease::getActiveCases).reversed();
                }
                else if (id == R.id.filter_cases_low) {
                    currentComparator = Comparator.comparingInt(Disease::getActiveCases);
                }

                applyFilters();
                return true;
            });

            popup.show();
        });
    }


    public void onDiseaseClick(Disease disease) {
        DiseaseInfo detailFragment = new DiseaseInfo();

        Bundle bundle = new Bundle();
        bundle.putSerializable("disease_data", disease);
        detailFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout, detailFragment)
                .addToBackStack(null)
                .commit();
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
            boolean matchesSearch = disease.getName().toLowerCase().startsWith(currentSearchText.toLowerCase().trim()) ||
                    disease.getName().toLowerCase().contains(" " + currentSearchText.toLowerCase().trim());
            boolean matchesCategory = selectedCategory == null || disease.getCategory().equalsIgnoreCase(selectedCategory);

            if (matchesSearch && matchesCategory) {
                filteredList.add(disease);
            }
        }

        // apply Sorting
        if (currentComparator != null) {
            Collections.sort(filteredList, currentComparator);
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

        if (selectedBtn != null) {
            selectedBtn.setBackgroundColor(colorCoral);
            selectedBtn.setTextColor(Color.WHITE);
            selectedBtn.setStrokeWidth(0);
        }
    }
}