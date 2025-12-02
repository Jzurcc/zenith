package com.cc17.zenith;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    private final List<Patient> patients;
    private final List<Patient> patientsFiltered;
    private final OnPatientClickListener onPatientClickListener;

    public PatientAdapter(List<Patient> patients, OnPatientClickListener onPatientClickListener) {
        this.patients = patients;
        this.patientsFiltered = new ArrayList<>(patients);
        this.onPatientClickListener = onPatientClickListener;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_item, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patientsFiltered.get(position);

        String profileImageUri = patient.getProfileImage();
        if (profileImageUri != null && !profileImageUri.isEmpty()) {
            holder.profileImage.setImageURI(Uri.parse(profileImageUri));
        } else {
            holder.profileImage.setImageResource(R.drawable.default_profile_pic);
        }

        if (!patient.getMiddleInitial().isEmpty()) {
            holder.patientName.setText(String.format("%s, %s %s.", patient.getLastName(), patient.getFirstName(), patient.getMiddleInitial()));
        } else {
            holder.patientName.setText(String.format("%s, %s", patient.getLastName(), patient.getFirstName()));
        }


        String sexDisplay = patient.getSex();
        if (sexDisplay == null || sexDisplay.trim().isEmpty()) {
            sexDisplay = "N/A";
        }

        String contactLabel;
        String contactValue;

        if (patient.getPrimaryPhoneNumber() != null && !patient.getPrimaryPhoneNumber().trim().isEmpty()) {
            contactLabel = patient.getPrimaryPhoneLabel();
            contactValue = patient.getPrimaryPhoneNumber();
            // fallback if label is empty
            if (contactLabel == null || contactLabel.isEmpty()) contactLabel = "Mobile";

        } else if (patient.getSecondaryPhoneNumber() != null && !patient.getSecondaryPhoneNumber().trim().isEmpty()) {
            contactLabel = patient.getSecondaryPhoneLabel();
            contactValue = patient.getSecondaryPhoneNumber();
            if (contactLabel == null || contactLabel.isEmpty()) contactLabel = "Phone";

        } else if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
            contactLabel = "Email";
            contactValue = patient.getEmail();

        } else {
            contactLabel = "Contact";
            contactValue = "N/A";
        }

        holder.patientDetails.setText(String.format("%s, %s\nMRN: %s\n%s: %s",
                sexDisplay,
                patient.getAge(),
                patient.getMrn(),
                contactLabel,
                contactValue));
        holder.itemView.setOnClickListener(v -> onPatientClickListener.onPatientClick(patient));
    }

    @Override
    public int getItemCount() {
        return patientsFiltered.size();
    }

    public void updateList(List<Patient> newPatients) {
        this.patients.clear();
        this.patients.addAll(newPatients);

        List<Patient> oldList = new ArrayList<>(this.patientsFiltered);
        this.patientsFiltered.clear();
        this.patientsFiltered.addAll(newPatients);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PatientDiffCallback(oldList, this.patientsFiltered));
        diffResult.dispatchUpdatesTo(this);
    }

    public void filter(String query) {
        List<Patient> oldList = new ArrayList<>(patientsFiltered);
        patientsFiltered.clear();
        if (query.isEmpty()) {
            patientsFiltered.addAll(patients);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Patient patient : patients) {
                // Check Name, MRN, or Phone
                if (patient.getLastName().toLowerCase().contains(lowerCaseQuery) ||
                        patient.getFirstName().toLowerCase().contains(lowerCaseQuery) ||
                        patient.getMrn().contains(lowerCaseQuery) ||
                        (patient.getPrimaryPhoneNumber() != null && patient.getPrimaryPhoneNumber().contains(lowerCaseQuery))) {

                    patientsFiltered.add(patient);
                }
            }
        }
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PatientDiffCallback(oldList, patientsFiltered));
        diffResult.dispatchUpdatesTo(this);
    }

    public void sort(java.util.Comparator<Patient> comparator) {
        List<Patient> oldList = new ArrayList<>(patientsFiltered);
        patientsFiltered.sort(comparator);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PatientDiffCallback(oldList, patientsFiltered));
        diffResult.dispatchUpdatesTo(this);
    }

    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView patientName;
        TextView patientDetails;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.patient_profile_image);
            patientName = itemView.findViewById(R.id.patient_name);
            patientDetails = itemView.findViewById(R.id.patient_details);
        }
    }

    public interface OnPatientClickListener {
        void onPatientClick(Patient patient);
    }

    private static class PatientDiffCallback extends DiffUtil.Callback {
        private final List<Patient> oldList;
        private final List<Patient> newList;

        PatientDiffCallback(List<Patient> oldList, List<Patient> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getMrn().equals(newList.get(newItemPosition).getMrn());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Patient oldPatient = oldList.get(oldItemPosition);
            Patient newPatient = newList.get(newItemPosition);
            return Objects.equals(oldPatient.getProfileImage(), newPatient.getProfileImage()) &&
                    Objects.equals(oldPatient.getLastName(), newPatient.getLastName()) &&
                    Objects.equals(oldPatient.getFirstName(), newPatient.getFirstName()) &&
                    Objects.equals(oldPatient.getMiddleInitial(), newPatient.getMiddleInitial()) &&
                    Objects.equals(oldPatient.getSex(), newPatient.getSex()) &&
                    Objects.equals(oldPatient.getAge(), newPatient.getAge()) &&
                    Objects.equals(oldPatient.getMrn(), newPatient.getMrn()) &&
                    Objects.equals(oldPatient.getPrimaryPhoneLabel(), newPatient.getPrimaryPhoneLabel()) &&
                    Objects.equals(oldPatient.getPrimaryPhoneNumber(), newPatient.getPrimaryPhoneNumber()) &&
                    Objects.equals(oldPatient.getSecondaryPhoneLabel(), newPatient.getSecondaryPhoneLabel()) &&
                    Objects.equals(oldPatient.getSecondaryPhoneNumber(), newPatient.getSecondaryPhoneNumber()) &&
                    Objects.equals(oldPatient.getEmail(), newPatient.getEmail());
        }
    }


}
