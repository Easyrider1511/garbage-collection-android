package com.garbagecollection.app.ui.map

import android.Manifest
import android.widget.Spinner
import android.widget.TextView
import com.garbagecollection.app.R
import com.garbagecollection.app.testsupport.FakeApiService
import com.garbagecollection.app.testsupport.FragmentTestActivity
import com.garbagecollection.app.testsupport.RetrofitClientRule
import com.garbagecollection.app.testsupport.TestFixtures
import com.garbagecollection.app.testsupport.useActivity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.osmdroid.views.MapView
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MapFragmentTest {

    @get:Rule
    val retrofitClientRule = RetrofitClientRule()

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
        shadowOf(TestFixtures.appContext()).grantPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        TestFixtures.setLastKnownLocation(latitude = 41.1, longitude = -8.6)
        retrofitClientRule.fakeApiService.allCollectionPointsResponse = Response.success(
            listOf(
                FakeApiService.sampleCollectionPoint(id = 1L, collectionTypes = listOf("PAPER")),
                FakeApiService.sampleCollectionPoint(id = 2L, collectionTypes = listOf("GLASS"))
            )
        )
    }

    @Test
    fun `loads collection points and updates summary when a map filter is selected`() {
        Robolectric.buildActivity(FragmentTestActivity::class.java).useActivity { activity ->
            val fragment = MapFragment()

            activity.supportFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment)
                .commitNow()
            TestFixtures.idleMainLooper()

            assertEquals(
                "2 of 2 points shown",
                activity.findViewById<TextView>(R.id.tvMapFilterSummary).text
            )

            activity.findViewById<Spinner>(R.id.spinnerCollectionTypeFilter).setSelection(4)
            TestFixtures.idleMainLooper()

            assertEquals(
                "1 of 2 point shown",
                activity.findViewById<TextView>(R.id.tvMapFilterSummary).text
            )
        }
    }

    @Test
    fun `map zoom buttons change the current zoom level`() {
        Robolectric.buildActivity(FragmentTestActivity::class.java).useActivity { activity ->
            activity.supportFragmentManager.beginTransaction()
                .add(android.R.id.content, MapFragment())
                .commitNow()
            TestFixtures.idleMainLooper()

            val mapView = activity.findViewById<MapView>(R.id.mapView)
            val initialZoom = mapView.zoomLevelDouble

            activity.findViewById<android.view.View>(R.id.btnMapZoomIn).performClick()
            TestFixtures.idleMainLooper()
            assertEquals(initialZoom + 1.0, mapView.zoomLevelDouble, 0.0001)

            activity.findViewById<android.view.View>(R.id.btnMapZoomOut).performClick()
            TestFixtures.idleMainLooper()
            assertEquals(initialZoom, mapView.zoomLevelDouble, 0.0001)
        }
    }

    @Test
    fun `shows empty filter summary when the backend returns no collection points`() {
        retrofitClientRule.fakeApiService.allCollectionPointsResponse = Response.success(emptyList())
        Robolectric.buildActivity(FragmentTestActivity::class.java).useActivity { activity ->
            activity.supportFragmentManager.beginTransaction()
                .add(android.R.id.content, MapFragment())
                .commitNow()
            TestFixtures.idleMainLooper()

            assertEquals(
                "No collection points available",
                activity.findViewById<TextView>(R.id.tvMapFilterSummary).text
            )
        }
    }
}
