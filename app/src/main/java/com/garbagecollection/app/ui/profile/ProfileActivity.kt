package com.garbagecollection.app.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.garbagecollection.app.R
import com.garbagecollection.app.api.RetrofitClient
import com.garbagecollection.app.databinding.ActivityProfileBinding
import com.garbagecollection.app.model.RegisterRequest
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.title_edit_profile)

        loadProfile()
        binding.btnSave.setOnClickListener { saveProfile() }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(this@ProfileActivity).getProfile()
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    binding.etFullName.setText(user.fullName)
                    binding.etEmail.setText(user.email)
                    binding.etPhone.setText(user.phoneNumber ?: "")
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, R.string.message_error_loading_profile, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveProfile() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etNewPassword.text.toString().trim()

        if (fullName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, R.string.message_name_email_required, Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSave.isEnabled = false
        val request = RegisterRequest(
            // Username is immutable in this edit flow; the backend keeps the current account username.
            username = "",
            email = email,
            password = password,
            fullName = fullName,
            phoneNumber = phone.ifEmpty { null }
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(this@ProfileActivity).updateProfile(request)
                if (response.isSuccessful) {
                    Toast.makeText(this@ProfileActivity, R.string.message_profile_updated, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@ProfileActivity, R.string.message_update_failed, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                val message = e.localizedMessage ?: getString(R.string.message_request_failed)
                Toast.makeText(
                    this@ProfileActivity,
                    getString(R.string.message_error, message),
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.btnSave.isEnabled = true
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
