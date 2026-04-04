package com.garbagecollection.app.ui.incidents

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.garbagecollection.app.R
import com.garbagecollection.app.api.RetrofitClient
import com.garbagecollection.app.databinding.ActivityCreateIncidentBinding
import com.garbagecollection.app.model.CreateIncidentRequest
import com.garbagecollection.app.util.DeviceLocationProvider
import com.garbagecollection.app.util.IncidentPhotoManager
import kotlinx.coroutines.launch

class CreateIncidentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateIncidentBinding
    private var currentLat: Double = 39.8228
    private var currentLng: Double = -7.4931
    private lateinit var incidentTypeValues: Array<String>
    private lateinit var severityLevelValues: Array<String>
    private var selectedPhotoUri: Uri? = null
    private var pendingPhotoUri: Uri? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            openCamera()
        } else {
            Toast.makeText(this, R.string.message_camera_permission_required, Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingPhotoUri != null) {
            selectedPhotoUri = pendingPhotoUri
            showSelectedPhoto()
        } else {
            pendingPhotoUri = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateIncidentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.title_report_incident)

        incidentTypeValues = resources.getStringArray(R.array.incident_type_values)
        severityLevelValues = resources.getStringArray(R.array.severity_level_values)

        binding.spinnerType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.incident_type_labels).toList()
        )
        binding.spinnerSeverity.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.severity_level_labels).toList()
        )

        getCurrentLocation()

        binding.btnUseCurrentLocation.setOnClickListener { getCurrentLocation() }
        binding.btnCapturePhoto.setOnClickListener { capturePhoto() }
        binding.btnRemovePhoto.setOnClickListener { clearSelectedPhoto() }
        binding.btnSubmit.setOnClickListener { submitIncident() }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        val location = DeviceLocationProvider.getLastKnownLocation(this)
        if (location != null) {
            currentLat = location.latitude
            currentLng = location.longitude
            binding.tvLocation.text = getString(R.string.location_coordinates, currentLat, currentLng)
        } else {
            Toast.makeText(this, R.string.message_location_unavailable, Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitIncident() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        if (title.isEmpty()) {
            binding.etTitle.error = getString(R.string.message_required)
            return
        }

        binding.btnSubmit.isEnabled = false

        val request = CreateIncidentRequest(
            title = title,
            description = description.ifEmpty { null },
            latitude = currentLat,
            longitude = currentLng,
            address = address.ifEmpty { null },
            type = incidentTypeValues[binding.spinnerType.selectedItemPosition],
            severity = severityLevelValues[binding.spinnerSeverity.selectedItemPosition],
            photoUrl = selectedPhotoUri?.toString()
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(this@CreateIncidentActivity)
                    .createIncident(request)
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateIncidentActivity, R.string.message_incident_reported, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CreateIncidentActivity, R.string.message_failed_to_submit, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                val message = e.localizedMessage ?: getString(R.string.message_request_failed)
                Toast.makeText(
                    this@CreateIncidentActivity,
                    getString(R.string.message_error, message),
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.btnSubmit.isEnabled = true
            }
        }
    }

    private fun capturePhoto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val photoUri = IncidentPhotoManager.createPhotoUri(this)
        pendingPhotoUri = photoUri
        takePictureLauncher.launch(photoUri)
    }

    private fun showSelectedPhoto() {
        val photoUri = selectedPhotoUri ?: return
        binding.ivPhotoPreview.visibility = View.VISIBLE
        binding.btnRemovePhoto.visibility = View.VISIBLE
        Glide.with(this)
            .load(photoUri)
            .centerCrop()
            .into(binding.ivPhotoPreview)
    }

    private fun clearSelectedPhoto() {
        selectedPhotoUri = null
        pendingPhotoUri = null
        binding.ivPhotoPreview.setImageDrawable(null)
        binding.ivPhotoPreview.visibility = View.GONE
        binding.btnRemovePhoto.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
