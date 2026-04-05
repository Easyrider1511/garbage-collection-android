package com.garbagecollection.app.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.garbagecollection.app.R
import com.garbagecollection.app.api.RetrofitClient
import com.garbagecollection.app.databinding.ActivityAdminDashboardBinding
import com.garbagecollection.app.model.CollectionPointDTO
import com.garbagecollection.app.model.CreateCollectionPointRequest
import com.garbagecollection.app.model.IncidentDTO
import com.garbagecollection.app.model.PickupScheduleDTO
import com.garbagecollection.app.util.UiTextFormatter
import java.text.NumberFormat
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var collectionTypeValues: Array<String>
    private lateinit var incidentStatusValues: Array<String>
    private lateinit var scheduleStatusValues: Array<String>
    private var incidents: List<IncidentDTO> = emptyList()
    private var schedules: List<PickupScheduleDTO> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.title_admin_dashboard)

        setupSpinners()
        bindActions()
        loadDashboard()
    }

    private fun setupSpinners() {
        collectionTypeValues = resources.getStringArray(R.array.collection_point_type_values)
        incidentStatusValues = resources.getStringArray(R.array.admin_incident_status_values)
        scheduleStatusValues = resources.getStringArray(R.array.admin_schedule_status_values)

        binding.spinnerCollectionPointType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.collection_point_type_labels).toList()
        )
        binding.spinnerIncidentStatus.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.admin_incident_status_labels).toList()
        )
        binding.spinnerScheduleStatus.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.admin_schedule_status_labels).toList()
        )
    }

    private fun bindActions() {
        binding.btnRefreshDashboard.setOnClickListener { loadDashboard() }
        binding.btnCreateCollectionPoint.setOnClickListener { createCollectionPoint() }
        binding.btnUpdateIncidentStatus.setOnClickListener { updateIncidentStatus() }
        binding.btnUpdateScheduleStatus.setOnClickListener { updateScheduleStatus() }
    }

    private fun loadDashboard() {
        binding.progressAdmin.visibility = View.VISIBLE
        binding.btnRefreshDashboard.isEnabled = false

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.getApiService(this@AdminDashboardActivity)
                val collectionPointsDeferred = async { api.getAllCollectionPoints() }
                val incidentsDeferred = async { api.getAllIncidents() }
                val schedulesDeferred = async { api.getAllSchedules() }

                val collectionPointsResponse = collectionPointsDeferred.await()
                val incidentsResponse = incidentsDeferred.await()
                val schedulesResponse = schedulesDeferred.await()

                val collectionPoints = collectionPointsResponse.body().orEmpty()
                    .sortedByDescending { it.id }
                incidents = incidentsResponse.body().orEmpty().sortedByDescending { it.id }
                schedules = schedulesResponse.body().orEmpty().sortedByDescending { it.id }

                bindSummary(collectionPoints, incidents, schedules)
                bindLatestItems(collectionPoints, incidents, schedules)
                bindIncidentSelector(incidents)
                bindScheduleSelector(schedules)
            } catch (error: Exception) {
                val message = error.localizedMessage ?: getString(R.string.message_request_failed)
                Toast.makeText(
                    this@AdminDashboardActivity,
                    getString(R.string.message_error, message),
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.progressAdmin.visibility = View.GONE
                binding.btnRefreshDashboard.isEnabled = true
            }
        }
    }

    private fun bindSummary(
        collectionPoints: List<CollectionPointDTO>,
        incidents: List<IncidentDTO>,
        schedules: List<PickupScheduleDTO>
    ) {
        val openIncidents = incidents.count {
            it.resolutionStatus !in setOf("RESOLVED", "CLOSED", "REJECTED")
        }
        val pendingSchedules = schedules.count {
            it.status !in setOf("COMPLETED", "CANCELLED")
        }

        binding.tvAdminTotalPoints.text = formatCount(collectionPoints.size)
        binding.tvAdminOpenIncidents.text = formatCount(openIncidents)
        binding.tvAdminPendingSchedules.text = formatCount(pendingSchedules)
    }

    private fun bindLatestItems(
        collectionPoints: List<CollectionPointDTO>,
        incidents: List<IncidentDTO>,
        schedules: List<PickupScheduleDTO>
    ) {
        binding.tvLatestCollectionPoint.text = collectionPoints.firstOrNull()?.let {
            "${it.name} · ${UiTextFormatter.collectionPointStatus(this, it.status)}"
        } ?: getString(R.string.admin_no_collection_points)

        binding.tvLatestIncident.text = incidents.firstOrNull()?.let {
            "#${it.id} · ${it.title} · ${UiTextFormatter.incidentStatus(this, it.resolutionStatus)}"
        } ?: getString(R.string.admin_no_incidents)

        binding.tvLatestSchedule.text = schedules.firstOrNull()?.let {
            "#${it.id} · ${it.description} · ${UiTextFormatter.scheduleStatus(this, it.status)}"
        } ?: getString(R.string.admin_no_schedules)
    }

    private fun bindIncidentSelector(incidents: List<IncidentDTO>) {
        val options = incidents
            .map { incident ->
                getString(
                    R.string.admin_incident_option,
                    incident.id,
                    incident.title,
                    UiTextFormatter.incidentStatus(this, incident.resolutionStatus)
                )
            }
            .ifEmpty { listOf(getString(R.string.admin_no_incidents)) }

        binding.spinnerIncidentTarget.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            options
        )
        binding.spinnerIncidentTarget.isEnabled = incidents.isNotEmpty()
        binding.btnUpdateIncidentStatus.isEnabled = incidents.isNotEmpty()
        binding.spinnerIncidentTarget.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    renderSelectedIncidentDetails(incidents.getOrNull(position))
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    renderSelectedIncidentDetails(null)
                }
            }
        renderSelectedIncidentDetails(incidents.firstOrNull())
    }

    private fun bindScheduleSelector(schedules: List<PickupScheduleDTO>) {
        val options = schedules
            .map { schedule ->
                getString(
                    R.string.admin_schedule_option,
                    schedule.id,
                    schedule.description,
                    UiTextFormatter.scheduleStatus(this, schedule.status)
                )
            }
            .ifEmpty { listOf(getString(R.string.admin_no_schedules)) }

        binding.spinnerScheduleTarget.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            options
        )
        binding.spinnerScheduleTarget.isEnabled = schedules.isNotEmpty()
        binding.btnUpdateScheduleStatus.isEnabled = schedules.isNotEmpty()
        binding.spinnerScheduleTarget.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    renderSelectedScheduleDetails(schedules.getOrNull(position))
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    renderSelectedScheduleDetails(null)
                }
            }
        renderSelectedScheduleDetails(schedules.firstOrNull())
    }

    private fun renderSelectedIncidentDetails(incident: IncidentDTO?) {
        binding.tvSelectedIncidentDetails.text = incident?.let {
            getString(
                R.string.admin_selected_incident_details,
                UiTextFormatter.incidentType(this, it.type),
                UiTextFormatter.severity(this, it.severity),
                it.address?.ifBlank { null } ?: getString(R.string.no_address_provided),
                it.reportCount,
                it.adminNotes?.ifBlank { null } ?: getString(R.string.no_admin_notes_yet)
            )
        } ?: getString(R.string.admin_no_incidents)
    }

    private fun renderSelectedScheduleDetails(schedule: PickupScheduleDTO?) {
        binding.tvSelectedScheduleDetails.text = schedule?.let {
            getString(
                R.string.admin_selected_schedule_details,
                UiTextFormatter.pickupItemType(this, it.itemType),
                it.address?.ifBlank { null } ?: getString(R.string.no_address_provided),
                it.scheduledDate?.ifBlank { null } ?: getString(R.string.pending_scheduling),
                it.adminNotes?.ifBlank { null } ?: getString(R.string.no_admin_notes_yet)
            )
        } ?: getString(R.string.admin_no_schedules)

        binding.etScheduleDate.setText(schedule?.scheduledDate.orEmpty())
    }

    private fun createCollectionPoint() {
        val name = binding.etCollectionPointName.text.toString().trim()
        val description = binding.etCollectionPointDescription.text.toString().trim()
        val address = binding.etCollectionPointAddress.text.toString().trim()
        val latitude = binding.etCollectionPointLatitude.text.toString().trim().toDoubleOrNull()
        val longitude = binding.etCollectionPointLongitude.text.toString().trim().toDoubleOrNull()

        if (name.isEmpty() || latitude == null || longitude == null) {
            Toast.makeText(this, R.string.message_fill_required_fields, Toast.LENGTH_SHORT).show()
            return
        }

        val request = CreateCollectionPointRequest(
            name = name,
            description = description.ifEmpty { null },
            latitude = latitude,
            longitude = longitude,
            address = address.ifEmpty { null },
            collectionTypes = listOf(
                collectionTypeValues[binding.spinnerCollectionPointType.selectedItemPosition]
            )
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(this@AdminDashboardActivity)
                    .createCollectionPoint(request)
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@AdminDashboardActivity,
                        R.string.message_collection_point_created,
                        Toast.LENGTH_SHORT
                    ).show()
                    clearCollectionPointForm()
                    loadDashboard()
                } else {
                    Toast.makeText(
                        this@AdminDashboardActivity,
                        R.string.message_failed_to_submit,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (error: Exception) {
                val message = error.localizedMessage ?: getString(R.string.message_request_failed)
                Toast.makeText(
                    this@AdminDashboardActivity,
                    getString(R.string.message_error, message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateIncidentStatus() {
        val incidentId = incidents
            .getOrNull(binding.spinnerIncidentTarget.selectedItemPosition)
            ?.id
        val adminNotes = binding.etIncidentAdminNotes.text.toString().trim()

        if (incidentId == null) {
            Toast.makeText(this, R.string.message_required, Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(this@AdminDashboardActivity)
                    .updateIncidentStatus(
                        incidentId,
                        incidentStatusValues[binding.spinnerIncidentStatus.selectedItemPosition],
                        adminNotes.ifEmpty { null }
                    )
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@AdminDashboardActivity,
                        R.string.message_incident_status_updated,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.etIncidentAdminNotes.text?.clear()
                    loadDashboard()
                } else {
                    Toast.makeText(
                        this@AdminDashboardActivity,
                        R.string.message_update_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (error: Exception) {
                val message = error.localizedMessage ?: getString(R.string.message_request_failed)
                Toast.makeText(
                    this@AdminDashboardActivity,
                    getString(R.string.message_error, message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateScheduleStatus() {
        val scheduleId = schedules
            .getOrNull(binding.spinnerScheduleTarget.selectedItemPosition)
            ?.id
        val scheduledDate = binding.etScheduleDate.text.toString().trim()
        val adminNotes = binding.etScheduleAdminNotes.text.toString().trim()

        if (scheduleId == null) {
            Toast.makeText(this, R.string.message_required, Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(this@AdminDashboardActivity)
                    .updateScheduleStatus(
                        scheduleId,
                        scheduleStatusValues[binding.spinnerScheduleStatus.selectedItemPosition],
                        scheduledDate.ifEmpty { null },
                        adminNotes.ifEmpty { null }
                    )
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@AdminDashboardActivity,
                        R.string.message_schedule_status_updated,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.etScheduleDate.text?.clear()
                    binding.etScheduleAdminNotes.text?.clear()
                    loadDashboard()
                } else {
                    Toast.makeText(
                        this@AdminDashboardActivity,
                        R.string.message_update_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (error: Exception) {
                val message = error.localizedMessage ?: getString(R.string.message_request_failed)
                Toast.makeText(
                    this@AdminDashboardActivity,
                    getString(R.string.message_error, message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun clearCollectionPointForm() {
        binding.etCollectionPointName.text?.clear()
        binding.etCollectionPointDescription.text?.clear()
        binding.etCollectionPointAddress.text?.clear()
        binding.etCollectionPointLatitude.text?.clear()
        binding.etCollectionPointLongitude.text?.clear()
        binding.spinnerCollectionPointType.setSelection(0)
    }

    private fun formatCount(value: Int): String =
        NumberFormat.getIntegerInstance(resources.configuration.locales[0]).format(value.toLong())

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
