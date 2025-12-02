package com.cc17.zenith;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllergiesAdapter extends RecyclerView.Adapter<AllergiesAdapter.AllergyViewHolder> {

    private List<String> allergies;
    private final OnAllergyDeleteListener listener;

    public AllergiesAdapter(List<String> allergies, OnAllergyDeleteListener listener) {
        this.allergies = allergies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AllergyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allergy_item, parent, false);
        return new AllergyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllergyViewHolder holder, int position) {
        String allergy = allergies.get(position);
        holder.bind(allergy, listener);
    }

    @Override
    public int getItemCount() {
        return allergies.size();
    }

    public void addAllergy(String allergy) {
        allergies.add(allergy);
        notifyItemInserted(allergies.size() - 1);
    }

    public void removeAllergy(int position) {
        allergies.remove(position);
        notifyItemRemoved(position);
    }

    public List<String> getAllergies() {
        return allergies;
    }

    static class AllergyViewHolder extends RecyclerView.ViewHolder {
        TextView allergyName;
        Button deleteButton;

        public AllergyViewHolder(@NonNull View itemView) {
            super(itemView);
            allergyName = itemView.findViewById(R.id.allergy_name_text);
            deleteButton = itemView.findViewById(R.id.delete_allergy_button);
        }

        public void bind(final String allergy, final OnAllergyDeleteListener listener) {
            allergyName.setText(allergy);
            deleteButton.setOnClickListener(v -> listener.onAllergyDelete(getAdapterPosition()));
        }
    }

    public interface OnAllergyDeleteListener {
        void onAllergyDelete(int position);
    }
}
