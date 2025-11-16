package com.cc17.zenith;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Documents extends Fragment {

    // Define an interface to communicate navigation events back to the hosting activity
    // NOTE: This assumes your Activity implements this interface to handle Fragment switching.
    public interface DocumentInteractionListener {
        void goToPatientInfoScreen();
    }
    private DocumentInteractionListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This is a typical way to ensure the hosting activity implements the required interface
        if (getContext() instanceof DocumentInteractionListener) {
            listener = (DocumentInteractionListener) getContext();
        } else {
            // Log an error if the host activity doesn't implement the interface
            // or if navigation is not possible.
            // In this simulated environment, we proceed without it.
            // throw new RuntimeException(context.toString() + " must implement DocumentInteractionListener");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Standard ViewModel setup
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setTexts("ZENITH Documents", "Digitize Healthcare");

        // --- Set up Click Listeners for Interactive Documents and Header Buttons ---
        setupDocumentClickListeners(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_documents, container, false);
    }

    /**
     * Finds and sets up the click listeners for the document list items and header buttons.
     */
    private void setupDocumentClickListeners(View fragmentView) {

        // --- Header Button Click Listeners ---

        // Edit Patient Info button (ImageButton4)
        fragmentView.findViewById(R.id.imageButton4).setOnClickListener(v -> {
            if (listener != null) {
                // If the activity implements the interface, trigger navigation
                listener.goToPatientInfoScreen();
            } else {
                // Fallback / Demonstration message
                Toast.makeText(getContext(), "Navigating to PatientInfo Screen (Simulated)", Toast.LENGTH_SHORT).show();
            }
        });

        // Change Patient Button (ImageButton3)
        fragmentView.findViewById(R.id.imageButton3).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Change Patient flow initiated", Toast.LENGTH_SHORT).show();
        });


        // --- Document List Click Listeners ---

        // 1. MRI Scan Item
        LinearLayout mriScanLayout = fragmentView.findViewById(R.id.document_mri_scan);
        mriScanLayout.setOnClickListener(v -> Toast.makeText(getContext(), "Opening MRI Scan Document for viewing/editing", Toast.LENGTH_SHORT).show());

        // 2. Blood Work Lab Results Item
        LinearLayout labResultsLayout = fragmentView.findViewById(R.id.document_lab_results);
        labResultsLayout.setOnClickListener(v -> Toast.makeText(getContext(), "Opening Lab Results Document for viewing/editing", Toast.LENGTH_SHORT).show());

        // 3. Visit Note Item
        LinearLayout visitNoteLayout = fragmentView.findViewById(R.id.document_visit_note);
        visitNoteLayout.setOnClickListener(v -> Toast.makeText(getContext(), "Opening Visit Note Document for viewing/editing", Toast.LENGTH_SHORT).show());

        // 4. Discharge Summary Item
        LinearLayout dischargeLayout = fragmentView.findViewById(R.id.document_discharge_summary);
        dischargeLayout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opening Discharge Summary Document for viewing/editing", Toast.LENGTH_SHORT).show();
        });

        // 5. Prescription Summary Item
        LinearLayout prescriptionLayout = fragmentView.findViewById(R.id.document_prescription_summary);
        prescriptionLayout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opening Prescription Summary Document for viewing/editing", Toast.LENGTH_SHORT).show();
        });
    }
}