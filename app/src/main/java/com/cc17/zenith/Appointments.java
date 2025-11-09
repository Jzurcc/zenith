package com.cc17.zenith;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class Appointments extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);
        ImageView julian = view.findViewById(R.id.julian);
        ImageView camille = view.findViewById(R.id.camille);
        ImageView angela1 = view.findViewById(R.id.angela1);
        ImageView angela2 = view.findViewById(R.id.angela2);
        julian.setOnClickListener(v -> {
            Toast.makeText(getContext(), "ERROR 404: Appointment not found!", Toast.LENGTH_SHORT).show();
        });
        camille.setOnClickListener(v -> {
            Toast.makeText(getContext(), "ERROR 404: Appointment not found!", Toast.LENGTH_SHORT).show();
        });
        angela1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "ERROR 404: Appointment not found!", Toast.LENGTH_SHORT).show();
        });
        angela2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "ERROR 404: Appointment not found!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setTexts("ZENITH Appointments", "View Incoming Patients");
    }
}