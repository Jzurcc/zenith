package com.cc17.zenith;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class PatientInfo extends Fragment {

    // Helper method to find EditText and set text safely
    // This method signature is designed to work with your provided code snippet.
    private void setEditText(View view, int id, String text) {
        EditText et = view.findViewById(id);
        if (et != null && text != null) {
            et.setText(text);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setTexts("Patient Information", "Centralize Patients");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_info, container, false);

        // --- 1. Set Click Listeners for Interactive Elements ---

        // The 'sex' button listener you originally provided
        ImageButton sex = view.findViewById(R.id.sex);
        sex.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Sex selection toggled", Toast.LENGTH_SHORT).show();
        });

        // Other ImageButtons
        view.findViewById(R.id.button2).setOnClickListener(v -> Toast.makeText(getActivity(), "Organ Donor status toggled", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.button3).setOnClickListener(v -> Toast.makeText(getActivity(), "Living Will status toggled", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.button4).setOnClickListener(v -> Toast.makeText(getActivity(), "Personal Email status toggled", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.button5).setOnClickListener(v -> Toast.makeText(getActivity(), "Same Mail Address status toggled", Toast.LENGTH_SHORT).show());

        // QR Code ImageView is also clickable
        view.findViewById(R.id.imageView32).setOnClickListener(v -> Toast.makeText(getActivity(), "Generating QR code...", Toast.LENGTH_SHORT).show());

        // --- 2. Retrieve and Populate Data ---
        Bundle args = getArguments();
        if (args != null) {

            // Basic Info Fields
            setEditText(view, R.id.et_first_name, args.getString("firstName"));
            setEditText(view, R.id.et_middle_name, args.getString("middleInitial"));
            setEditText(view, R.id.et_last_name, args.getString("lastName"));
            // Preferred name field is typically left empty unless data exists for it

            // Detailed Info Fields (Matching the image snippet and preceding logic)
            setEditText(view, R.id.et_dob, args.getString("dob"));
            setEditText(view, R.id.et_country_birth, args.getString("countryOfBirth"));
            setEditText(view, R.id.et_city_birth, args.getString("cityOfBirth"));
            setEditText(view, R.id.et_province_birth, args.getString("provinceOfBirth"));

            setEditText(view, R.id.et_marital_status, args.getString("maritalStatus"));
            setEditText(view, R.id.et_race_ethnicity, args.getString("raceEthnicity"));
            setEditText(view, R.id.et_mrn, args.getString("mrn"));
            setEditText(view, R.id.et_fin, args.getString("mrn")); // Assuming FIN uses MRN value for sample data

            setEditText(view, R.id.et_occupation, args.getString("occupation"));
            setEditText(view, R.id.et_employer, args.getString("employer"));
            setEditText(view, R.id.et_education, args.getString("education"));

            setEditText(view, R.id.et_religion, args.getString("religion"));
            setEditText(view, R.id.et_preferences, args.getString("preferences"));
            setEditText(view, R.id.et_lang_record, args.getString("langRecord"));
            setEditText(view, R.id.et_lang_record_no, args.getString("langRecordNo"));

            // Contact/Address Fields
            setEditText(view, R.id.et_email, args.getString("email"));

            setEditText(view, R.id.et_address_line1, args.getString("address1"));
            setEditText(view, R.id.et_city_address, args.getString("city"));
            setEditText(view, R.id.et_province_address, args.getString("province"));

            setEditText(view, R.id.et_address_line2, null); // Placeholder for Address Line 2
            setEditText(view, R.id.et_zipcode, args.getString("zip"));
            setEditText(view, R.id.et_region, args.getString("region"));
            setEditText(view, R.id.et_country, args.getString("country"));

            setEditText(view, R.id.et_primary_phone, args.getString("primaryPhone"));
            setEditText(view, R.id.et_secondary_phone, args.getString("secondaryPhone"));

            setEditText(view, R.id.et_remarks, null); // Placeholder for Remarks

            // Note: Boolean fields (isOrganDonor, isLivingWill, etc.) would require specific logic to toggle the ImageButton background drawable.
        }

        return view;
    }
}