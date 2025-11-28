package com.cc17.zenith;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    private List<Patient> patients;
    private List<Patient> patientsFiltered;
    private OnPatientClickListener onPatientClickListener;

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
        holder.profileImage.setImageResource(patient.getProfileImage());
        holder.patientName.setText(String.format("%s, %s %s.", patient.getLastName(), patient.getFirstName(), patient.getMiddleInitial()));
        holder.patientDetails.setText(String.format("%s, %s\nID No.: %s\nMobile No.: %s", patient.getSex(), patient.getAge(), patient.getIdNo(), patient.getMobileNo()));
        holder.itemView.setOnClickListener(v -> onPatientClickListener.onPatientClick(patient));
    }

    @Override
    public int getItemCount() {
        return patientsFiltered.size();
    }

    public void filter(String query) {
        patientsFiltered.clear();
        if (query.isEmpty()) {
            patientsFiltered.addAll(patients);
        } else {
            for (Patient patient : patients) {
                if (patient.getLastName().toLowerCase().contains(query.toLowerCase()) ||
                    patient.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                    patient.getIdNo().toLowerCase().contains(query.toLowerCase()) ||
                    patient.getMobileNo().toLowerCase().contains(query.toLowerCase())) {
                    patientsFiltered.add(patient);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void sort(java.util.Comparator<Patient> comparator) {
        patientsFiltered.sort(comparator);
        notifyDataSetChanged();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
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
}
