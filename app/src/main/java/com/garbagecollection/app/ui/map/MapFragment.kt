package com.garbagecollection.app.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.garbagecollection.app.R
import com.garbagecollection.app.api.RetrofitClient
import com.garbagecollection.app.databinding.FragmentMapBinding
import com.garbagecollection.app.model.CollectionPointDTO
import com.garbagecollection.app.util.CollectionPointFilter
import com.garbagecollection.app.util.DeviceLocationProvider
import com.garbagecollection.app.util.UiTextFormatter
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var myLocationOverlay: MyLocationNewOverlay? = null
    private val markerOverlays = mutableListOf<Marker>()
    private var collectionPoints: List<CollectionPointDTO> = emptyList()
    private lateinit var mapFilterTypeValues: Array<String>
    private var selectedCollectionType: String = CollectionPointFilter.ALL_TYPES

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            enableMyLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        configureOsmDroid()
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runCatching {
            setupMap()
            setupFilters()
            checkLocationPermission()
            loadCollectionPoints()
        }.onFailure { error ->
            val message = error.localizedMessage ?: getString(R.string.message_request_failed)
            showMapMessage(getString(R.string.map_load_failed_message, message))
        }
    }

    override fun onResume() {
        super.onResume()
        _binding?.mapView?.onResume()
    }

    override fun onPause() {
        _binding?.mapView?.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        markerOverlays.clear()
        myLocationOverlay?.disableFollowLocation()
        myLocationOverlay?.disableMyLocation()
        myLocationOverlay = null
        binding.mapView.onDetach()
        _binding = null
        super.onDestroyView()
    }

    private fun configureOsmDroid() {
        val appContext = requireContext().applicationContext
        Configuration.getInstance().load(
            appContext,
            PreferenceManager.getDefaultSharedPreferences(appContext)
        )
        Configuration.getInstance().userAgentValue = appContext.packageName
    }

    private fun setupMap() {
        val mapView = binding.mapView
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(DEFAULT_ZOOM)
        mapView.controller.setCenter(DEFAULT_LOCATION)
        showMap()
    }

    private fun setupFilters() {
        mapFilterTypeValues = resources.getStringArray(R.array.map_filter_type_values)
        binding.spinnerCollectionTypeFilter.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.map_filter_type_labels).toList()
        )
        binding.spinnerCollectionTypeFilter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCollectionType = mapFilterTypeValues[position]
                    displayMarkers()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
    }

    private fun checkLocationPermission() {
        if (DeviceLocationProvider.hasLocationPermission(requireContext())) {
            enableMyLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun enableMyLocation() {
        if (!DeviceLocationProvider.hasLocationPermission(requireContext())) {
            return
        }

        if (myLocationOverlay == null) {
            myLocationOverlay = MyLocationNewOverlay(
                GpsMyLocationProvider(requireContext()),
                binding.mapView
            )
            binding.mapView.overlays.add(myLocationOverlay)
        }

        myLocationOverlay?.enableMyLocation()

        DeviceLocationProvider.getLastKnownLocation(requireContext())?.let { location ->
            binding.mapView.controller.animateTo(
                GeoPoint(location.latitude, location.longitude),
                USER_LOCATION_ZOOM,
                ANIMATION_DURATION_MS
            )
        }
    }

    private fun loadCollectionPoints() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(requireContext()).getAllCollectionPoints()
                if (response.isSuccessful && response.body() != null) {
                    collectionPoints = response.body()!!
                    displayMarkers()
                } else {
                    collectionPoints = emptyList()
                    displayMarkers()
                }
            } catch (e: Exception) {
                val message = e.localizedMessage ?: getString(R.string.message_request_failed)
                Toast.makeText(
                    context,
                    getString(R.string.message_error_loading_points, message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun displayMarkers() {
        val mapView = binding.mapView
        markerOverlays.forEach { mapView.overlays.remove(it) }
        markerOverlays.clear()

        val visiblePoints = CollectionPointFilter.filterByType(
            collectionPoints,
            selectedCollectionType
        )

        for (point in visiblePoints) {
            val marker = Marker(mapView).apply {
                position = GeoPoint(point.latitude, point.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = point.name
                snippet = getString(
                    R.string.map_marker_snippet,
                    point.collectionTypes.joinToString(", ") {
                        UiTextFormatter.collectionType(requireContext(), it)
                    },
                    UiTextFormatter.collectionPointStatus(requireContext(), point.status)
                )
                icon = createMarkerIcon(point)
            }

            mapView.overlays.add(marker)
            markerOverlays.add(marker)
        }

        binding.tvMapFilterSummary.text = if (collectionPoints.isEmpty()) {
            getString(R.string.map_filter_summary_zero)
        } else {
            resources.getQuantityString(
                R.plurals.map_filter_summary,
                visiblePoints.size,
                visiblePoints.size,
                collectionPoints.size
            )
        }
        mapView.invalidate()
    }

    private fun createMarkerIcon(point: CollectionPointDTO): Drawable? {
        val markerDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_map_marker)
            ?: return null

        val tintColor = when {
            point.collectionTypes.contains("BATTERIES") || point.collectionTypes.contains("ELECTRONICS") ->
                "#D32F2F".toColorInt()
            point.collectionTypes.contains("BULKY_ITEMS") ->
                "#F57C00".toColorInt()
            point.collectionTypes.contains("PAPER") || point.collectionTypes.contains("GLASS") || point.collectionTypes.contains("PLASTIC") ->
                "#2E7D32".toColorInt()
            else -> "#0288D1".toColorInt()
        }

        return DrawableCompat.wrap(markerDrawable.mutate()).apply {
            DrawableCompat.setTint(this, tintColor)
        }
    }

    private fun showMapMessage(message: String) {
        binding.mapView.visibility = View.GONE
        binding.mapMessageContainer.visibility = View.VISIBLE
        binding.tvMapMessageBody.text = message
    }

    private fun showMap() {
        binding.mapView.visibility = View.VISIBLE
        binding.mapMessageContainer.visibility = View.GONE
    }

    companion object {
        private val DEFAULT_LOCATION = GeoPoint(39.8228, -7.4931)
        private const val DEFAULT_ZOOM = 14.0
        private const val USER_LOCATION_ZOOM = 15.0
        private const val ANIMATION_DURATION_MS = 1_000L
    }
}
