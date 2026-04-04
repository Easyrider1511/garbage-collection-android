package com.garbagecollection.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.garbagecollection.app.R
import com.garbagecollection.app.api.RetrofitClient
import com.garbagecollection.app.databinding.ActivityRegisterBinding
import com.garbagecollection.app.model.RegisterRequest
import com.garbagecollection.app.ui.MainActivity
import com.garbagecollection.app.util.SessionManager
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.btnRegister.setOnClickListener { performRegister() }
        binding.tvLogin.setOnClickListener { finish() }
    }

    private fun performRegister() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val fullName = binding.etFullName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            Toast.makeText(this, R.string.message_fill_required_fields, Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, R.string.message_password_min_length, Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnRegister.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(this@RegisterActivity)
                    .register(RegisterRequest(username, email, password, fullName, phone.ifEmpty { null }))

                if (response.isSuccessful && response.body() != null) {
                    val auth = response.body()!!
                    sessionManager.saveSession(auth.token, auth.username, auth.role, auth.userId)
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finishAffinity()
                } else {
                    Toast.makeText(this@RegisterActivity, R.string.message_registration_failed, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                val message = e.localizedMessage ?: getString(R.string.message_request_failed)
                Toast.makeText(
                    this@RegisterActivity,
                    getString(R.string.message_error, message),
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.btnRegister.isEnabled = true
            }
        }
    }
}
