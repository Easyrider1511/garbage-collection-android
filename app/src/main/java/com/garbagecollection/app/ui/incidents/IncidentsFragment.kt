package com.garbagecollection.app.ui.incidents

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.garbagecollection.app.R
import com.garbagecollection.app.adapter.IncidentAdapter
import com.garbagecollection.app.api.RetrofitClient
import com.garbagecollection.app.databinding.FragmentIncidentsBinding
import com.garbagecollection.app.util.SessionManager
import kotlinx.coroutines.launch

class IncidentsFragment : Fragment() {

    private var _binding: FragmentIncidentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: IncidentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIncidentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = IncidentAdapter { incident ->
            val intent = Intent(requireContext(), IncidentDetailActivity::class.java)
            intent.putExtra("incident_id", incident.id)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.fabCreateIncident.setOnClickListener {
            startActivity(Intent(requireContext(), CreateIncidentActivity::class.java))
        }

        binding.swipeRefresh.setOnRefreshListener { loadIncidents() }

        loadIncidents()
    }

    override fun onResume() {
        super.onResume()
        loadIncidents()
    }

    private fun loadIncidents() {
        lifecycleScope.launch {
            try {
                binding.swipeRefresh.isRefreshing = true
                val userId = SessionManager(requireContext()).getUserId() ?: 0L
                val response = RetrofitClient.getApiService(requireContext()).getMyIncidents(userId)
                if (response.isSuccessful && response.body() != null) {
                    val incidents = response.body()!!
                    adapter.submitList(incidents)
                    binding.tvEmpty.visibility = if (incidents.isEmpty()) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) {
                val message = e.localizedMessage ?: getString(R.string.message_request_failed)
                Toast.makeText(context, getString(R.string.message_error_loading_incidents, message), Toast.LENGTH_SHORT).show()
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
