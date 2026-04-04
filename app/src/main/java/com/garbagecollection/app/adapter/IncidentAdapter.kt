package com.garbagecollection.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.garbagecollection.app.R
import com.garbagecollection.app.databinding.ItemIncidentBinding
import com.garbagecollection.app.model.IncidentDTO
import com.garbagecollection.app.util.UiTextFormatter

class IncidentAdapter(
    private val onItemClick: (IncidentDTO) -> Unit
) : ListAdapter<IncidentDTO, IncidentAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemIncidentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(incident: IncidentDTO) {
            binding.tvTitle.text = incident.title
            binding.tvType.text = UiTextFormatter.incidentType(binding.root.context, incident.type)
            binding.tvSeverity.text = UiTextFormatter.severity(binding.root.context, incident.severity)
            binding.tvStatus.text = UiTextFormatter.incidentStatus(binding.root.context, incident.resolutionStatus)
            binding.tvReports.text = binding.root.resources.getQuantityString(
                R.plurals.report_count,
                incident.reportCount,
                incident.reportCount
            )

            val severityColor = when (incident.severity) {
                "CRITICAL" -> 0xFFD32F2F.toInt()
                "HIGH" -> 0xFFF57C00.toInt()
                "MEDIUM" -> 0xFFFFA000.toInt()
                else -> 0xFF388E3C.toInt()
            }
            binding.tvSeverity.setTextColor(severityColor)

            val statusColor = when (incident.resolutionStatus) {
                "RESOLVED", "CLOSED" -> 0xFF388E3C.toInt()
                "IN_PROGRESS" -> 0xFF1976D2.toInt()
                else -> 0xFFF57C00.toInt()
            }
            binding.tvStatus.setTextColor(statusColor)

            binding.root.setOnClickListener { onItemClick(incident) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIncidentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<IncidentDTO>() {
        override fun areItemsTheSame(a: IncidentDTO, b: IncidentDTO) = a.id == b.id
        override fun areContentsTheSame(a: IncidentDTO, b: IncidentDTO) = a == b
    }
}
