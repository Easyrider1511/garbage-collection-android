package com.garbagecollection.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.garbagecollection.app.R
import com.garbagecollection.app.api.RetrofitClient
import com.garbagecollection.app.databinding.ActivityLoginBinding
import com.garbagecollection.app.model.LoginRequest
import com.garbagecollection.app.ui.MainActivity
import com.garbagecollection.app.util.AppLanguageManager
import com.garbagecollection.app.util.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Auto-login if session exists
        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        setupLanguageSelector()
        binding.btnLogin.setOnClickListener { performLogin() }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupLanguageSelector() {
        updateLanguageSelector()

        binding.languageToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) {
                return@addOnButtonCheckedListener
            }

            val selectedLanguage = when (checkedId) {
                R.id.btnLanguagePortuguese -> AppLanguageManager.LANGUAGE_PT_PT
                else -> AppLanguageManager.LANGUAGE_EN
            }

            if (selectedLanguage != AppLanguageManager.getSavedLanguageTag(this)) {
                AppLanguageManager.applyLanguage(this, selectedLanguage)
            }
        }
    }

    private fun updateLanguageSelector() {
        val checkedButtonId = when (AppLanguageManager.getSavedLanguageTag(this)) {
            AppLanguageManager.LANGUAGE_PT_PT -> R.id.btnLanguagePortuguese
            else -> R.id.btnLanguageEnglish
        }

        if (binding.languageToggleGroup.checkedButtonId != checkedButtonId) {
            binding.languageToggleGroup.check(checkedButtonId)
        }
    }

    private fun performLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.message_fill_all_fields, Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnLogin.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(this@LoginActivity)
                    .login(LoginRequest(username, password))

                if (response.isSuccessful && response.body() != null) {
                    val auth = response.body()!!
                    sessionManager.saveSession(auth.token, auth.username, auth.role, auth.userId)
                    navigateToMain()
                } else {
                    Toast.makeText(this@LoginActivity, R.string.message_invalid_credentials, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                val message = e.localizedMessage ?: getString(R.string.message_request_failed)
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.message_connection_error, message),
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.btnLogin.isEnabled = true
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
