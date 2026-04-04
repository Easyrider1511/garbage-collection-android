package com.garbagecollection.app.ui.schedules

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
import com.garbagecollection.app.adapter.ScheduleAdapter
import com.garbagecollection.app.api.RetrofitClient
import com.garbagecollection.app.databinding.FragmentSchedulesBinding
import com.garbagecollection.app.util.SessionManager
import kotlinx.coroutines.launch

class SchedulesFragment : Fragment() {

    private var _binding: FragmentSchedulesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ScheduleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSchedulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ScheduleAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.fabCreateSchedule.setOnClickListener {
            startActivity(Intent(requireContext(), CreateScheduleActivity::class.java))
        }

        binding.swipeRefresh.setOnRefreshListener { loadSchedules() }
        loadSchedules()
    }

    override fun onResume() {
        super.onResume()
        loadSchedules()
    }

    private fun loadSchedules() {
        lifecycleScope.launch {
            try {
                binding.swipeRefresh.isRefreshing = true
                val userId = SessionManager(requireContext()).getUserId() ?: 0L
                val response = RetrofitClient.getApiService(requireContext()).getMySchedules(userId)
                if (response.isSuccessful && response.body() != null) {
                    val schedules = response.body()!!
                    adapter.submitList(schedules)
                    binding.tvEmpty.visibility = if (schedules.isEmpty()) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) {
                val message = e.localizedMessage ?: getString(R.string.message_request_failed)
                Toast.makeText(context, getString(R.string.message_error_loading_schedules, message), Toast.LENGTH_SHORT).show()
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
