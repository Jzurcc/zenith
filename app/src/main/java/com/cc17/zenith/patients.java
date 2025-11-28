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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class patients extends Fragment implements PatientAdapter.OnPatientClickListener {

    private PatientAdapter patientAdapter;
    private List<Patient> patients;

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

        setupRecyclerView(view);
        setupSearchAndFilter(view);
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.patient_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        patients = new ArrayList<>();
        patients.add(new Patient("Julian", "R", "Alvarez", "46", "Male", "6202158", "0965 0568 555",
                "12/21/1979", "Philippines", "Baguio City", "Benguet",
                "Married", "Ilocano", "200365448", "Architect",
                "Visionarch", "College Graduate", "Christian", "N/A",
                "English", "1977232", true, true,
                "julianalvarez@gmail.com", "142 Holy Ghost Hill Ext. Rd.", "Baguio",
                "Benguet", "2600", "CAR", "Philippines",
                "(63+) 927 910 7392", "(214) 723-9001", R.drawable.alvarez_profile));
        patients.add(new Patient("Angela", "M", "Bautista", "41", "Female", "6203174", "0917 2256 432",
                "10/15/1984", "Philippines", "Manila", "Metro Manila",
                "Single", "Tagalog", "200365449", "Designer",
                "Self-Employed", "High School Graduate", "Catholic", "None",
                "Tagalog", "1000001", false, false,
                "angelabautista@gmail.com", "123 Main St.", "Manila",
                "Metro Manila", "1000", "NCR", "Philippines",
                "(63+) 917 2256 432", "(02) 8123-4567", R.drawable.bautista_profile));
        patients.add(new Patient("Michael", "J", "Cruz", "42", "Male", "6204180", "0995 8457 210",
                "05/01/1983", "Philippines", "Cebu City", "Cebu",
                "Married", "Cebuano", "200365450", "Engineer",
                "MegaWorld", "Masters Degree", "Catholic", "N/A",
                "Cebuano", "2000002", true, false,
                "michaelcruz@gmail.com", "456 Ocean View Rd.", "Cebu",
                "Cebu", "6000", "VII", "Philippines",
                "(63+) 995 8457 210", "(032) 567-8901", R.drawable.cruz_profile));
        patients.add(new Patient("Camille", "A", "Dela Rosa", "27", "Female", "6202198", "0921 7789 654",
                "11/20/1998", "Philippines", "Davao City", "Davao Del Sur",
                "Single", "Bicolano", "200365451", "Student",
                "None", "Undergraduate", "Atheist", "Vegan",
                "English", "3000003", false, false,
                "camille.dela.rosa@gmail.com", "789 Pine Tree Lane", "Davao",
                "Davao Del Sur", "8000", "XI", "Philippines",
                "(63+) 921 7789 654", "N/A", R.drawable.dela_rosa_profile));

        patientAdapter = new PatientAdapter(patients, this);
        recyclerView.setAdapter(patientAdapter);
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
        Bundle bundle = new Bundle();
        bundle.putString("firstName", patient.getFirstName());
        bundle.putString("middleInitial", patient.getMiddleInitial());
        bundle.putString("lastName", patient.getLastName());
        bundle.putString("age", patient.getAge());
        bundle.putString("sex", patient.getSex());
        bundle.putString("idNo", patient.getIdNo());
        bundle.putString("mobileNo", patient.getMobileNo());
        bundle.putString("dob", patient.getDob());
        bundle.putString("countryOfBirth", patient.getCountryOfBirth());
        bundle.putString("cityOfBirth", patient.getCityOfBirth());
        bundle.putString("provinceOfBirth", patient.getProvinceOfBirth());
        bundle.putString("maritalStatus", patient.getMaritalStatus());
        bundle.putString("raceEthnicity", patient.getRaceEthnicity());
        bundle.putString("mrn", patient.getMrn());
        bundle.putString("occupation", patient.getOccupation());
        bundle.putString("employer", patient.getEmployer());
        bundle.putString("education", patient.getEducation());
        bundle.putString("religion", patient.getReligion());
        bundle.putString("preferences", patient.getPreferences());
        bundle.putString("langRecord", patient.getLangRecord());
        bundle.putString("langRecordNo", patient.getLangRecordNo());
        bundle.putBoolean("isOrganDonor", patient.isOrganDonor());
        bundle.putBoolean("isLivingWill", patient.isLivingWill());
        bundle.putString("email", patient.getEmail());
        bundle.putString("address1", patient.getAddress1());
        bundle.putString("city", patient.getCity());
        bundle.putString("province", patient.getProvince());
        bundle.putString("zip", patient.getZip());
        bundle.putString("region", patient.getRegion());
        bundle.putString("country", patient.getCountry());
        bundle.putString("primaryPhone", patient.getPrimaryPhone());
        bundle.putString("secondaryPhone", patient.getSecondaryPhone());

        Documents documentsFragment = new Documents();
        documentsFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, documentsFragment)
                .addToBackStack(null)
                .commit();
    }
}
