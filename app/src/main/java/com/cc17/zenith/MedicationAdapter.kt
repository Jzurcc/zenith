package com.cc17.zenith

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicationAdapter(
    private val meds: MutableList<Medication>,
    private val listener: OnMedicationActionListener // Added Listener
) : RecyclerView.Adapter<MedicationAdapter.MedViewHolder>() {

    // Interface for click events
    interface OnMedicationActionListener {
        fun onEdit(med: Medication, position: Int)
        fun onDelete(position: Int)
    }

    // Function to ADD an item
    fun addMedication(med: Medication) {
        meds.add(0, med)
        notifyItemInserted(0)
    }

    // Function to UPDATE an item (For Edit Dialog)
    fun updateMedication(position: Int, med: Medication) {
        if (position >= 0 && position < meds.size) {
            meds[position] = med
            notifyItemChanged(position)
        }
    }

    // Function to REMOVE an item
    fun removeMedication(position: Int) {
        if (position >= 0 && position < meds.size) {
            meds.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, meds.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medication, parent, false)
        return MedViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedViewHolder, position: Int) {
        val med = meds[position]
        holder.tvName.text = med.name
        holder.tvInstruct.text = med.instruction
        holder.tvCategory.text = med.category

        // Handle Edit Click
        holder.ivEdit.setOnClickListener {
            listener.onEdit(med, position)
        }

        // Handle Delete Click
        holder.ivDelete.setOnClickListener {
            // We pass this back to fragment if we want central control,
            // or just handle it here. For consistency, let's notify the listener
            // or keep using internal logic. Let's use internal logic + listener for future proofing
            removeMedication(holder.adapterPosition)
            listener.onDelete(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = meds.size

    class MedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvDrugName)
        val tvInstruct: TextView = itemView.findViewById(R.id.tvInstructions)
        val tvCategory: TextView = itemView.findViewById(R.id.chipCategory)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit) // Added ID for edit
    }
}