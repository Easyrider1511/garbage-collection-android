package com.garbagecollection.app.util

import android.content.Context
import com.garbagecollection.app.R
import java.util.Locale

object UiTextFormatter {

    fun incidentType(context: Context, value: String): String {
        return when (normalizeCode(value)) {
            "MISSING_CONTAINER" -> context.getString(R.string.incident_type_missing_container)
            "VANDALIZED_CONTAINER" -> context.getString(R.string.incident_type_vandalized_container)
            "HAZARDOUS_SPILL" -> context.getString(R.string.incident_type_hazardous_spill)
            "OVERFLOW" -> context.getString(R.string.incident_type_overflow)
            "ILLEGAL_DUMPING" -> context.getString(R.string.incident_type_illegal_dumping)
            "DAMAGED_EQUIPMENT" -> context.getString(R.string.incident_type_damaged_equipment)
            "OTHER" -> context.getString(R.string.item_other)
            else -> humanizeValue(value)
        }
    }

    fun severity(context: Context, value: String): String {
        return when (normalizeCode(value)) {
            "LOW" -> context.getString(R.string.severity_low)
            "MEDIUM" -> context.getString(R.string.severity_medium)
            "HIGH" -> context.getString(R.string.severity_high)
            "CRITICAL" -> context.getString(R.string.severity_critical)
            else -> humanizeValue(value)
        }
    }

    fun incidentStatus(context: Context, value: String): String {
        return when (normalizeCode(value)) {
            "OPEN" -> context.getString(R.string.status_open)
            "PENDING" -> context.getString(R.string.status_pending)
            "IN_PROGRESS" -> context.getString(R.string.status_in_progress)
            "RESOLVED" -> context.getString(R.string.status_resolved)
            "CLOSED" -> context.getString(R.string.status_closed)
            "REJECTED" -> context.getString(R.string.status_rejected)
            else -> humanizeValue(value)
        }
    }

    fun scheduleStatus(context: Context, value: String): String {
        return when (normalizeCode(value)) {
            "PENDING" -> context.getString(R.string.status_pending)
            "APPROVED" -> context.getString(R.string.status_approved)
            "SCHEDULED" -> context.getString(R.string.status_scheduled)
            "COMPLETED" -> context.getString(R.string.status_completed)
            "CANCELLED" -> context.getString(R.string.status_cancelled)
            "REJECTED" -> context.getString(R.string.status_rejected)
            else -> humanizeValue(value)
        }
    }

    fun pickupItemType(context: Context, value: String): String {
        return when (normalizeWords(value)) {
            "FURNITURE" -> context.getString(R.string.pickup_item_furniture)
            "ELECTRONICS" -> context.getString(R.string.pickup_item_electronics)
            "APPLIANCES" -> context.getString(R.string.pickup_item_appliances)
            "MATTRESS" -> context.getString(R.string.pickup_item_mattress)
            "CONSTRUCTION DEBRIS" -> context.getString(R.string.pickup_item_construction_debris)
            "OTHER" -> context.getString(R.string.item_other)
            else -> humanizeValue(value)
        }
    }

    fun collectionType(context: Context, value: String): String {
        return when (normalizeCode(value)) {
            "BATTERIES" -> context.getString(R.string.collection_type_batteries)
            "ELECTRONICS" -> context.getString(R.string.collection_type_electronics)
            "BULKY_ITEMS" -> context.getString(R.string.collection_type_bulky_items)
            "PAPER" -> context.getString(R.string.collection_type_paper)
            "GLASS" -> context.getString(R.string.collection_type_glass)
            "PLASTIC" -> context.getString(R.string.collection_type_plastic)
            "ORGANIC" -> context.getString(R.string.collection_type_organic)
            "MIXED_WASTE" -> context.getString(R.string.collection_type_mixed_waste)
            "METAL" -> context.getString(R.string.collection_type_metal)
            "CLOTHES" -> context.getString(R.string.collection_type_clothes)
            "OTHER" -> context.getString(R.string.item_other)
            else -> humanizeValue(value)
        }
    }

    fun collectionPointStatus(context: Context, value: String): String {
        return when (normalizeCode(value)) {
            "ACTIVE" -> context.getString(R.string.status_active)
            "INACTIVE" -> context.getString(R.string.status_inactive)
            "FULL" -> context.getString(R.string.collection_point_status_full)
            "MAINTENANCE" -> context.getString(R.string.collection_point_status_maintenance)
            else -> humanizeValue(value)
        }
    }

    fun userRole(context: Context, value: String): String {
        return when (normalizeCode(value)) {
            "ROLE_ADMIN", "ADMIN" -> context.getString(R.string.role_admin)
            "ROLE_USER", "USER" -> context.getString(R.string.role_user)
            else -> humanizeValue(value)
        }
    }

    private fun normalizeCode(value: String): String {
        return value.trim().uppercase(Locale.ROOT).replace('-', '_')
    }

    private fun normalizeWords(value: String): String {
        return value.trim().uppercase(Locale.ROOT).replace('_', ' ')
    }

    private fun humanizeValue(value: String): String {
        val normalized = value.trim().replace('_', ' ').lowercase(Locale.ROOT)
        return normalized.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }
}
