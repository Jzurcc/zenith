package com.cc17.zenith;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


public class diseasetrends extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setTexts("Disease Trends", "Track Diseases");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diseasetrends, container, false);

        ImageButton dengue = view.findViewById(R.id.dengue);
        ImageButton influenza = view.findViewById(R.id.influenza);

        dengue.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "ERROR 404: Disease Page not found!", Toast.LENGTH_SHORT).show();
        });

        influenza.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "ERROR 404: Disease Page not found!", Toast.LENGTH_SHORT).show();
        });

        ImageButton tuberculosis = view.findViewById(R.id.tuberculosis);
        tuberculosis.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_layout, new DiseaseInfo())
                    .commit();
        });

        return view;
    }
}