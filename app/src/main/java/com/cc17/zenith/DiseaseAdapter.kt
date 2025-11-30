package com.cc17.zenith

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DiseaseAdapter(
    private var list: List<Disease>,
    private val listener: OnDiseaseClickListener?
) : RecyclerView.Adapter<DiseaseAdapter.DiseaseViewHolder>() {

    // Interface defined in Kotlin style
    interface OnDiseaseClickListener {
        fun onDiseaseClick(disease: Disease)
    }

    // Function to update the list dynamically
    fun updateList(newList: List<Disease>) {
        this.list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.disease_item, parent, false)
        return DiseaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiseaseViewHolder, position: Int) {
        val item = list[position]

        // Kotlin allows direct property access (item.name instead of item.getName())
        holder.tvName.text = item.name
        holder.tvDate.text = item.date
        holder.tvCount.text = item.activeCases.toString()

        // Set Click Listener
        holder.itemView.setOnClickListener {
            listener?.onDiseaseClick(item)
        }
    }

    override fun getItemCount(): Int = list.size

    class DiseaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvDiseaseName)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvCount: TextView = itemView.findViewById(R.id.tvCaseCount)
    }
}