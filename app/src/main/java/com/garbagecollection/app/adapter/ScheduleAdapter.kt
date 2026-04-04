package com.garbagecollection.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.garbagecollection.app.R
import com.garbagecollection.app.databinding.ItemScheduleBinding
import com.garbagecollection.app.model.PickupScheduleDTO
import com.garbagecollection.app.util.UiTextFormatter

class ScheduleAdapter : ListAdapter<PickupScheduleDTO, ScheduleAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(private val binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(schedule: PickupScheduleDTO) {
            binding.tvDescription.text = schedule.description
            binding.tvItemType.text = UiTextFormatter.pickupItemType(binding.root.context, schedule.itemType)
            binding.tvStatus.text = UiTextFormatter.scheduleStatus(binding.root.context, schedule.status)
            binding.tvAddress.text = schedule.address ?: binding.root.context.getString(
                R.string.address_coordinates_fallback,
                schedule.latitude,
                schedule.longitude
            )
            binding.tvScheduledDate.text = schedule.scheduledDate
                ?: binding.root.context.getString(R.string.pending_scheduling)

            val statusColor = when (schedule.status) {
                "COMPLETED" -> 0xFF388E3C.toInt()
                "SCHEDULED", "APPROVED" -> 0xFF1976D2.toInt()
                "CANCELLED" -> 0xFFD32F2F.toInt()
                else -> 0xFFF57C00.toInt()
            }
            binding.tvStatus.setTextColor(statusColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<PickupScheduleDTO>() {
        override fun areItemsTheSame(a: PickupScheduleDTO, b: PickupScheduleDTO) = a.id == b.id
        override fun areContentsTheSame(a: PickupScheduleDTO, b: PickupScheduleDTO) = a == b
    }
}
