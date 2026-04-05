package com.garbagecollection.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.garbagecollection.app.R
import com.garbagecollection.app.api.RetrofitClient
import com.garbagecollection.app.databinding.FragmentProfileBinding
import com.garbagecollection.app.ui.admin.AdminDashboardActivity
import com.garbagecollection.app.ui.auth.LoginActivity
import com.garbagecollection.app.util.AppLanguageManager
import com.garbagecollection.app.util.SessionManager
import com.garbagecollection.app.util.UiTextFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        binding.tvUsername.text = sessionManager.getUsername()
        binding.tvRole.text = sessionManager.getRole()
            ?.let { UiTextFormatter.userRole(requireContext(), it) }
            .orEmpty()
        updateLanguageUi()
        updateAdminDashboardVisibility(sessionManager.getRole())

        loadProfile()

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        binding.btnChangeLanguage.setOnClickListener {
            showLanguageDialog()
        }

        binding.btnAdminDashboard.setOnClickListener {
            startActivity(Intent(requireContext(), AdminDashboardActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finishAffinity()
        }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(requireContext()).getProfile()
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    binding.tvUsername.text = user.username
                    binding.tvFullName.text = user.fullName
                    binding.tvEmail.text = user.email
                    binding.tvPhone.text = user.phoneNumber ?: getString(R.string.label_not_set)
                    binding.tvRole.text = UiTextFormatter.userRole(requireContext(), user.role)
                    binding.tvStatus.text = if (user.active) {
                        getString(R.string.status_active)
                    } else {
                        getString(R.string.status_inactive)
                    }
                    sessionManager.updateRole(user.role)
                    updateAdminDashboardVisibility(user.role)
                }
            } catch (e: Exception) {
                Toast.makeText(context, R.string.message_error_loading_profile, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLanguageUi() {
        binding.tvLanguageValue.text = AppLanguageManager.getCurrentLanguageDisplayName(requireContext())
    }

    private fun updateAdminDashboardVisibility(role: String?) {
        binding.btnAdminDashboard.visibility = if (isAdminRole(role)) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun isAdminRole(role: String?): Boolean {
        val normalizedRole = role
            ?.trim()
            ?.uppercase(Locale.ROOT)
            ?.replace('-', '_')
            ?: return false
        return normalizedRole == "ADMIN" || normalizedRole == "ROLE_ADMIN"
    }

    private fun showLanguageDialog() {
        val languageTags = AppLanguageManager.supportedLanguageTags()
        val languageLabels = AppLanguageManager.supportedLanguageLabels(requireContext())
        val currentIndex = languageTags.indexOf(AppLanguageManager.getSavedLanguageTag(requireContext()))

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_select_language)
            .setSingleChoiceItems(languageLabels, currentIndex) { dialog, which ->
                dialog.dismiss()
                val selectedLanguage = languageTags[which]
                if (selectedLanguage != AppLanguageManager.getSavedLanguageTag(requireContext())) {
                    AppLanguageManager.applyLanguage(requireContext(), selectedLanguage)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        updateLanguageUi()
        loadProfile()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
