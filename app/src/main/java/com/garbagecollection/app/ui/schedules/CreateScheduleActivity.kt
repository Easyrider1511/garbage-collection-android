package com.garbagecollection.app.ui.schedules

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.garbagecollection.app.R
import com.garbagecollection.app.api.RetrofitClient
import com.garbagecollection.app.databinding.ActivityCreateScheduleBinding
import com.garbagecollection.app.model.CreatePickupRequest
import com.garbagecollection.app.util.DeviceLocationProvider
import kotlinx.coroutines.launch

class CreateScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateScheduleBinding
    private var currentLat: Double = 39.8228
    private var currentLng: Double = -7.4931
    private lateinit var itemTypeValues: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.title_schedule_pickup)

        itemTypeValues = resources.getStringArray(R.array.pickup_item_type_values)
        binding.spinnerItemType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.pickup_item_type_labels).toList()
        )

        getCurrentLocation()
        binding.btnUseLocation.setOnClickListener { getCurrentLocation() }
        binding.btnSubmit.setOnClickListener { submitSchedule() }
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

    private fun submitSchedule() {
        val description = binding.etDescription.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        if (description.isEmpty()) {
            binding.etDescription.error = getString(R.string.message_required)
            return
        }

        binding.btnSubmit.isEnabled = false
        val request = CreatePickupRequest(
            description = description,
            itemType = itemTypeValues[binding.spinnerItemType.selectedItemPosition],
            latitude = currentLat,
            longitude = currentLng,
            address = address.ifEmpty { null }
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(this@CreateScheduleActivity)
                    .createPickupSchedule(request)
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateScheduleActivity, R.string.message_pickup_scheduled, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CreateScheduleActivity, R.string.message_request_failed, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                val message = e.localizedMessage ?: getString(R.string.message_request_failed)
                Toast.makeText(
                    this@CreateScheduleActivity,
                    getString(R.string.message_error, message),
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.btnSubmit.isEnabled = true
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
