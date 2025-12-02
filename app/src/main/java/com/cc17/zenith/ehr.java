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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;

public class ehr extends Fragment implements MedicationAdapter.OnMedicationActionListener {
    private RecyclerView rvMedications;
    private MedicationAdapter adapter;
    private List<Medication> medicationList;

    public void MedicationFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setTexts("ZENITH Medication", "Review Prescriptions");

        rvMedications = view.findViewById(R.id.rvMedList);
        rvMedications.setLayoutManager(new LinearLayoutManager(getContext()));

        medicationList = new ArrayList<>();
        medicationList.add(new Medication("Niacin 500 MG", "1/d for Cholesterol", "Daily", "Oral"));
        medicationList.add(new Medication("Aspirin Enteric Coated", "1/d for heart health", "Daily", "Oral"));
        medicationList.add(new Medication("Bumetanide 1 MG", "2/d for CHF", "Daily", "Oral"));
        medicationList.add(new Medication("Ergocalciferol", "1/w day of week", "Weekly", "Oral"));
        medicationList.add(new Medication("Clonidine HCl", "1/w day of week", "Weekly", "Patch"));
        medicationList.add(new Medication("Levemir 100I", "1/w day of week", "Weekly", "Injection"));
        medicationList.add(new Medication("Losartan Potassium", "1/w day of week", "Daily", "Oral"));

        adapter = new MedicationAdapter(medicationList, this);
        rvMedications.setAdapter(adapter);

        ImageButton btnAdd = view.findViewById(R.id.btnAddMed);
        btnAdd.setOnClickListener(v -> showMedicationDialog(null, -1));

        ImageButton btnQR = view.findViewById(R.id.medicineQR);

        if (btnQR != null) {
            btnQR.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Generate QR Clicked", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void showMedicationDialog(@Nullable Medication medToEdit, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_medicine, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        // Make background transparent for rounded corners effect
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Initialize Dialog Views
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etName = dialogView.findViewById(R.id.etDrugName);
        EditText etInstruction = dialogView.findViewById(R.id.etInstruction);
        EditText etCategory = dialogView.findViewById(R.id.etCategory);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // SETUP FOR EDIT MODE vs ADD MODE
        if (medToEdit != null) {
            tvTitle.setText("Edit Medication");
            etName.setText(medToEdit.getName());
            etInstruction.setText(medToEdit.getInstruction());
            etCategory.setText(medToEdit.getCategory());
        } else {
            tvTitle.setText("Add Medication");
        }

        // SAVE BUTTON CLICK
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String instruct = etInstruction.getText().toString().trim();
            String cat = etCategory.getText().toString().trim();

            if (name.isEmpty() || instruct.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create Object
            Medication newMed = new Medication(name, instruct, cat.isEmpty() ? "Daily" : cat, "Oral");

            if (medToEdit != null) {
                // EDIT MODE: Update existing
                adapter.updateMedication(position, newMed);
            } else {
                // ADD MODE: Insert new
                adapter.addMedication(newMed);
                rvMedications.smoothScrollToPosition(0);
            }
            dialog.dismiss();
        });

        // CANCEL BUTTON
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // --- ADAPTER LISTENERS ---

    @Override
    public void onEdit(@NonNull Medication med, int position) {
        int realPosition = medicationList.indexOf(med);

        if (realPosition != -1) {
            showMedicationDialog(med, realPosition);
        } else {
            showMedicationDialog(med, position);
        }
    }

    @Override
    public void onDelete(int position) {
        Toast.makeText(getContext(), "Medication Removed", Toast.LENGTH_SHORT).show();
    }

    private void addNewRandomMed() {
        // Logic to simulate adding a new medication
        String[] sampleMeds = {"Metformin 500mg", "Lipitor 10mg", "Amoxicillin 250mg", "Ibuprofen 400mg"};
        String[] instructions = {"2/d with food", "1/d before bed", "3/d for 7 days", "As needed for pain"};

        Random random = new Random();
        String name = sampleMeds[random.nextInt(sampleMeds.length)];
        String instruct = instructions[random.nextInt(instructions.length)];

        Medication newMed = new Medication(name, instruct, "New", "Oral");

        // Use the Adapter's method to add it to the UI
        adapter.addMedication(newMed);

        // Scroll to top to see the new item
        rvMedications.smoothScrollToPosition(0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ehr, container, false);
    }
}