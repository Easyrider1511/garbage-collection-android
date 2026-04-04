package com.garbagecollection.app.ui.incidents

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.garbagecollection.app.R
import com.garbagecollection.app.api.RetrofitClient
import com.garbagecollection.app.databinding.ActivityIncidentDetailBinding
import com.garbagecollection.app.util.UiTextFormatter
import kotlinx.coroutines.launch

class IncidentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIncidentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncidentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.title_incident_details)

        val incidentId = intent.getLongExtra("incident_id", -1)
        if (incidentId == -1L) { finish(); return }

        loadIncident(incidentId)

        binding.btnReportSame.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.getApiService(this@IncidentDetailActivity)
                        .reportSameIncident(incidentId)
                    if (response.isSuccessful) {
                        Toast.makeText(this@IncidentDetailActivity, R.string.message_report_count_updated, Toast.LENGTH_SHORT).show()
                        loadIncident(incidentId)
                    }
                } catch (e: Exception) {
                    val message = e.localizedMessage ?: getString(R.string.message_request_failed)
                    Toast.makeText(
                        this@IncidentDetailActivity,
                        getString(R.string.message_error, message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun loadIncident(id: Long) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(this@IncidentDetailActivity).getIncident(id)
                if (response.isSuccessful && response.body() != null) {
                    val incident = response.body()!!
                    binding.tvTitle.text = incident.title
                    binding.tvDescription.text = incident.description ?: getString(R.string.no_description)
                    binding.tvType.text = UiTextFormatter.incidentType(this@IncidentDetailActivity, incident.type)
                    binding.tvSeverity.text = UiTextFormatter.severity(this@IncidentDetailActivity, incident.severity)
                    binding.tvStatus.text = UiTextFormatter.incidentStatus(this@IncidentDetailActivity, incident.resolutionStatus)
                    binding.tvLocation.text = getString(R.string.location_coordinates, incident.latitude, incident.longitude)
                    binding.tvAddress.text = incident.address ?: getString(R.string.no_address_provided)
                    binding.tvReportCount.text = resources.getQuantityString(
                        R.plurals.report_count,
                        incident.reportCount,
                        incident.reportCount
                    )
                    binding.tvCreatedAt.text = incident.createdAt ?: "-"
                    binding.tvAdminNotes.text = incident.adminNotes ?: getString(R.string.no_admin_notes_yet)
                    bindIncidentPhoto(incident.photoUrl)

                    // Color-code severity
                    val severityColor = when (incident.severity) {
                        "CRITICAL" -> 0xFFD32F2F.toInt()
                        "HIGH" -> 0xFFF57C00.toInt()
                        "MEDIUM" -> 0xFFFFA000.toInt()
                        else -> 0xFF388E3C.toInt()
                    }
                    binding.tvSeverity.setTextColor(severityColor)
                }
            } catch (e: Exception) {
                val message = e.localizedMessage ?: getString(R.string.message_request_failed)
                Toast.makeText(
                    this@IncidentDetailActivity,
                    getString(R.string.message_error, message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun bindIncidentPhoto(photoUrl: String?) {
        if (photoUrl.isNullOrBlank()) {
            binding.tvPhotoLabel.visibility = View.GONE
            binding.ivIncidentPhoto.visibility = View.GONE
            binding.ivIncidentPhoto.setImageDrawable(null)
            return
        }

        binding.tvPhotoLabel.visibility = View.VISIBLE
        binding.ivIncidentPhoto.visibility = View.VISIBLE
        Glide.with(this)
            .load(photoUrl)
            .centerCrop()
            .into(binding.ivIncidentPhoto)
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
