package com.cc17.zenith;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ehr extends Fragment implements MedicationAdapter.OnMedicationActionListener {

    private RecyclerView rvMedications;
    private MedicationAdapter adapter;
    private List<Medication> medicationList;

    private static final String PREFS_NAME = "ZenithPrefs";
    private static final String KEY_MEDICATIONS = "SavedMedications";

    public ehr() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ehr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            sharedViewModel.setTexts("ZENITH Medication", "Review Prescriptions");
        } catch (Exception e) {
            e.printStackTrace();
        }

        rvMedications = view.findViewById(R.id.rvMedList);
        rvMedications.setLayoutManager(new LinearLayoutManager(getContext()));

        medicationList = new ArrayList<>();
        loadMedications();

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

    private void saveMedications() {
        if (getContext() == null) return;
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray jsonArray = new JSONArray();
        for (Medication med : medicationList) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", med.getName());
                jsonObject.put("instruction", med.getInstruction());
                jsonObject.put("category", med.getCategory());
                jsonObject.put("type", med.getType());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        editor.putString(KEY_MEDICATIONS, jsonArray.toString());
        editor.apply();
    }

    private void loadMedications() {
        if (getContext() == null) return;
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String jsonString = prefs.getString(KEY_MEDICATIONS, null);

        if (jsonString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Medication med = new Medication(
                            obj.getString("name"),
                            obj.getString("instruction"),
                            obj.getString("category"),
                            obj.optString("type", "Oral")
                    );
                    medicationList.add(med);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            medicationList.add(new Medication("Niacin 500 MG", "1/d for Cholesterol", "Daily", "Oral"));
            medicationList.add(new Medication("Aspirin Enteric Coated", "1/d for heart health", "Daily", "Oral"));
            medicationList.add(new Medication("Bumetanide 1 MG", "2/d for CHF", "Daily", "Oral"));
            medicationList.add(new Medication("Ergocalciferol", "1/w day of week", "Weekly", "Oral"));
            medicationList.add(new Medication("Clonidine HCl", "1/w day of week", "Weekly", "Patch"));
            medicationList.add(new Medication("Levemir 100I", "1/w day of week", "Weekly", "Injection"));
            medicationList.add(new Medication("Losartan Potassium", "1/w day of week", "Daily", "Oral"));

            saveMedications();
        }
    }

    private void showMedicationDialog(@Nullable Medication medToEdit, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_medicine, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etName = dialogView.findViewById(R.id.etDrugName);
        EditText etInstruction = dialogView.findViewById(R.id.etInstruction);
        EditText etCategory = dialogView.findViewById(R.id.etCategory);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        if (medToEdit != null) {
            tvTitle.setText("Edit Medication");
            etName.setText(medToEdit.getName());
            etInstruction.setText(medToEdit.getInstruction());
            etCategory.setText(medToEdit.getCategory());
        } else {
            tvTitle.setText("Add Medication");
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String instruct = etInstruction.getText().toString().trim();
            String cat = etCategory.getText().toString().trim();

            if (name.isEmpty() || instruct.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Medication newMed = new Medication(name, instruct, cat.isEmpty() ? "Daily" : cat, "Oral");

            if (medToEdit != null) {
                adapter.updateMedication(position, newMed);
            } else {
                adapter.addMedication(newMed);
                rvMedications.smoothScrollToPosition(0);
            }

            saveMedications();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

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
        saveMedications();
    }
}