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

    interface OnDiseaseClickListener {
        fun onDiseaseClick(disease: Disease)
    }

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

        holder.tvName.text = item.name
        holder.tvDate.text = item.date
        holder.tvCount.text = item.activeCases.toString()

        // Pass the specific trend data for this disease to the graph
        if (item.trendData != null && item.trendData.isNotEmpty()) {
            holder.graphTrend.setData(item.trendData)
            holder.graphTrend.visibility = View.VISIBLE
        } else {
            // Optional: Hide graph if no data exists
            holder.graphTrend.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener {
            listener?.onDiseaseClick(item)
        }
    }

    override fun getItemCount(): Int = list.size

    class DiseaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvDiseaseName)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvCount: TextView = itemView.findViewById(R.id.tvCaseCount)
        // Find the custom view instead of the ImageView
        val graphTrend: SimpleLineGraph = itemView.findViewById(R.id.graphTrend)
    }
}