package com.cc17.zenith;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class patients extends Fragment implements PatientAdapter.OnPatientClickListener {

    private PatientAdapter patientAdapter;
    private List<Patient> patientsList = new ArrayList<>();


    public patients() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patients, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setTexts("Dashboard", "Overview");

        // runs only once
        sharedViewModel.initializeDefaultPatients(requireContext());

        setupRecyclerView(view, sharedViewModel);
        setupSearchAndFilter(view);

        View btnAddPatient = view.findViewById(R.id.btn_add_patient);
        btnAddPatient.setOnClickListener(v -> {
            PatientInfo newPatientFragment = new PatientInfo();
            Bundle args = new Bundle();
            args.putBoolean("isNewPatient", true);
            newPatientFragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_layout, newPatientFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupRecyclerView(View view, SharedViewModel viewModel) {
        RecyclerView recyclerView = view.findViewById(R.id.patient_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        patientAdapter = new PatientAdapter(patientsList, this);
        recyclerView.setAdapter(patientAdapter);

        viewModel.getPatientList().observe(getViewLifecycleOwner(), updatedList -> {
            patientsList = updatedList;
            patientAdapter = new PatientAdapter(patientsList, this);
            recyclerView.setAdapter(patientAdapter);
        });
    }


    private void setupSearchAndFilter(View view) {
        SearchView searchView = view.findViewById(R.id.patient_search_view);
        searchView.setOnClickListener(v -> searchView.onActionViewExpanded());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                patientAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                patientAdapter.filter(newText);
                return false;
            }
        });

        ImageButton filterButton = view.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(this::showFilterMenu);
    }

    private void showFilterMenu(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.patient_filter_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.filter_name_asc) {
                patientAdapter.sort(Comparator.comparing(Patient::getLastName));
                return true;
            } else if (itemId == R.id.filter_name_desc) {
                patientAdapter.sort(Comparator.comparing(Patient::getLastName).reversed());
                return true;
            } else if (itemId == R.id.filter_age_asc) {
                patientAdapter.sort(Comparator.comparing(p -> {
                    try {
                        return Integer.parseInt(p.getAge());
                    } catch (NumberFormatException e) {
                        return 0; // Default to 0 if age is not a number
                    }
                }));
                return true;
            } else if (itemId == R.id.filter_age_desc) {
                patientAdapter.sort(Comparator.comparing((Patient p) -> {
                    try {
                        return Integer.parseInt(p.getAge());
                    } catch (NumberFormatException e) {
                        return 0; // Default to 0 if age is not a number
                    }
                }).reversed());
                return true;
            }
            return false;
        });
        popup.show();
    }

    @Override
    public void onPatientClick(Patient patient) {
        try {
            Bundle bundle = new Bundle();
            bundle.putParcelable("selected_patient", patient);

            Documents documentsFragment = new Documents();
            documentsFragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_layout, documentsFragment)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error opening patient: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
