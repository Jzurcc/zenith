package com.cc17.zenith;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout; // Used for patient list items

public class patients extends Fragment {

    // Mimicking the private static final fields for argument keys
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam2;

    public patients() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile.
     */
    public static patients newInstance(String param1, String param2) {
        patients fragment = new patients();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patients, container, false);

        // Find the clickable patient rows defined in XML (assuming LinearLayouts were used)
        LinearLayout alvarezRow = view.findViewById(R.id.patient_alvarez);
        LinearLayout bautistaRow = view.findViewById(R.id.patient_bautista);
        LinearLayout cruzRow = view.findViewById(R.id.patient_cruz);
        LinearLayout delaRosaRow = view.findViewById(R.id.patient_delarosa);


        // Setup click listeners for navigation and data passing
        alvarezRow.setOnClickListener(v -> navigateToPatientInfo(
                "Julian", "Ramos", "Alvarez", "46", "Male", "6202158", "0965 0568 555",
                "12/21/1979", "Philippines", "Baguio City", "Benguet",
                "Married", "Ilocano", "200365448", "Architect",
                "Visionarch", "College Graduate", "Christian", "N/A",
                "English", "1977232", true, true,
                "julianalvarez@gmail.com", "142 Holy Ghost Hill Ext. Rd.", "Baguio",
                "Benguet", "2600", "CAR", "Philippines",
                "(63+) 927 910 7392", "(214) 723-9001"
        ));

        bautistaRow.setOnClickListener(v -> navigateToPatientInfo(
                "Angela", "M", "Bautista", "41", "Female", "6203174", "0917 2256 432",
                "10/15/1984", "Philippines", "Manila", "Metro Manila",
                "Single", "Tagalog", "200365449", "Designer",
                "Self-Employed", "High School Graduate", "Catholic", "None",
                "Tagalog", "1000001", false, false,
                "angelabautista@gmail.com", "123 Main St.", "Manila",
                "Metro Manila", "1000", "NCR", "Philippines",
                "(63+) 917 2256 432", "(02) 8123-4567"
        ));

        cruzRow.setOnClickListener(v -> navigateToPatientInfo(
                "Michael", "J", "Cruz", "42", "Male", "6204180", "0995 8457 210",
                "05/01/1983", "Philippines", "Cebu City", "Cebu",
                "Married", "Cebuano", "200365450", "Engineer",
                "MegaWorld", "Masters Degree", "Catholic", "N/A",
                "Cebuano", "2000002", true, false,
                "michaelcruz@gmail.com", "456 Ocean View Rd.", "Cebu",
                "Cebu", "6000", "VII", "Philippines",
                "(63+) 995 8457 210", "(032) 567-8901"
        ));

        delaRosaRow.setOnClickListener(v -> navigateToPatientInfo(
                "Camille", "A", "Dela Rosa", "27", "Female", "6202198", "0921 7789 654",
                "11/20/1998", "Philippines", "Davao City", "Davao Del Sur",
                "Single", "Bicolano", "200365451", "Student",
                "None", "Undergraduate", "Atheist", "Vegan",
                "English", "3000003", false, false,
                "camille.dela.rosa@gmail.com", "789 Pine Tree Lane", "Davao",
                "Davao Del Sur", "8000", "XI", "Philippines",
                "(63+) 921 7789 654", "N/A"
        ));

        return view;
    }

    /**
     * Helper function to bundle and navigate with all patient data.
     */
    private void navigateToPatientInfo(
            String firstName, String middleInitial, String lastName, String age, String sex, String idNo, String mobileNo,
            String dob, String countryOfBirth, String cityOfBirth, String provinceOfBirth,
            String maritalStatus, String raceEthnicity, String mrn, String occupation,
            String employer, String education, String religion, String preferences,
            String langRecord, String langRecordNo, boolean isOrganDonor, boolean isLivingWill,
            String email, String address1, String city, String province, String zip, String region, String country,
            String primaryPhone, String secondaryPhone
    ) {
        Bundle bundle = new Bundle();

        // Basic Info
        bundle.putString("firstName", firstName);
        bundle.putString("middleInitial", middleInitial);
        bundle.putString("lastName", lastName);
        bundle.putString("age", age);
        bundle.putString("sex", sex);
        bundle.putString("idNo", idNo);
        bundle.putString("mobileNo", mobileNo);

        // Detailed Info
        bundle.putString("dob", dob);
        bundle.putString("countryOfBirth", countryOfBirth);
        bundle.putString("cityOfBirth", cityOfBirth);
        bundle.putString("provinceOfBirth", provinceOfBirth);
        bundle.putString("maritalStatus", maritalStatus);
        bundle.putString("raceEthnicity", raceEthnicity);
        bundle.putString("mrn", mrn);
        bundle.putString("occupation", occupation);
        bundle.putString("employer", employer);
        bundle.putString("education", education);
        bundle.putString("religion", religion);
        bundle.putString("preferences", preferences);
        bundle.putString("langRecord", langRecord);
        bundle.putString("langRecordNo", langRecordNo);
        bundle.putBoolean("isOrganDonor", isOrganDonor);
        bundle.putBoolean("isLivingWill", isLivingWill);

        // Contact/Address
        bundle.putString("email", email);
        bundle.putString("address1", address1);
        bundle.putString("city", city);
        bundle.putString("province", province);
        bundle.putString("zip", zip);
        bundle.putString("region", region);
        bundle.putString("country", country);
        bundle.putString("primaryPhone", primaryPhone);
        bundle.putString("secondaryPhone", secondaryPhone);

        var patientInfoFragment = new PatientInfo();
        patientInfoFragment.setArguments(bundle);

        // Navigate to the PatientInfo fragment (replace R.id.fragment_layout with your actual container ID)
        int commit = requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, patientInfoFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setTexts("Dashboard", "Overview");
    }
}