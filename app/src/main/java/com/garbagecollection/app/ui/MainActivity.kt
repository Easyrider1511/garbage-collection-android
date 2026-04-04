package com.garbagecollection.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.garbagecollection.app.R
import com.garbagecollection.app.databinding.ActivityMainBinding
import com.garbagecollection.app.ui.incidents.IncidentsFragment
import com.garbagecollection.app.ui.map.MapFragment
import com.garbagecollection.app.ui.profile.ProfileFragment
import com.garbagecollection.app.ui.schedules.SchedulesFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_map -> { loadFragment(MapFragment(), R.string.title_map); true }
                R.id.nav_incidents -> { loadFragment(IncidentsFragment(), R.string.title_incidents); true }
                R.id.nav_schedules -> { loadFragment(SchedulesFragment(), R.string.title_schedules); true }
                R.id.nav_profile -> { loadFragment(ProfileFragment(), R.string.title_profile); true }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            loadFragment(MapFragment(), R.string.title_map)
        } else {
            binding.bottomNavigation.post {
                updateToolbarTitle(binding.bottomNavigation.selectedItemId)
            }
        }
    }

    private fun loadFragment(fragment: Fragment, titleResId: Int) {
        supportActionBar?.setTitle(titleResId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateToolbarTitle(itemId: Int) {
        val titleResId = when (itemId) {
            R.id.nav_incidents -> R.string.title_incidents
            R.id.nav_schedules -> R.string.title_schedules
            R.id.nav_profile -> R.string.title_profile
            else -> R.string.title_map
        }
        supportActionBar?.setTitle(titleResId)
    }
}
