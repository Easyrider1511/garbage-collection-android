package com.garbagecollection.app.ui.admin

import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.garbagecollection.app.R
import com.garbagecollection.app.testsupport.FakeApiService
import com.garbagecollection.app.testsupport.RetrofitClientRule
import com.garbagecollection.app.testsupport.TestFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AdminDashboardActivityTest {

    @get:Rule
    val retrofitClientRule = RetrofitClientRule()

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
        TestFixtures.saveAdminSession()
        retrofitClientRule.fakeApiService.allCollectionPointsResponse = retrofit2.Response.success(
            listOf(
                FakeApiService.sampleCollectionPoint(id = 1L, status = "ACTIVE"),
                FakeApiService.sampleCollectionPoint(id = 2L, status = "FULL")
            )
        )
        retrofitClientRule.fakeApiService.allIncidentsResponse = retrofit2.Response.success(
            listOf(
                FakeApiService.sampleIncident(id = 1L, resolutionStatus = "OPEN"),
                FakeApiService.sampleIncident(id = 2L, resolutionStatus = "RESOLVED")
            )
        )
        retrofitClientRule.fakeApiService.allSchedulesResponse = retrofit2.Response.success(
            listOf(
                FakeApiService.sampleSchedule(id = 1L, status = "PENDING"),
                FakeApiService.sampleSchedule(id = 2L, status = "COMPLETED")
            )
        )
    }

    @Test
    fun `loads dashboard summary and latest items`() {
        val activity = Robolectric.buildActivity(AdminDashboardActivity::class.java).setup().get()
        TestFixtures.idleMainLooper()

        assertEquals("2", activity.findViewById<TextView>(R.id.tvAdminTotalPoints).text)
        assertEquals("1", activity.findViewById<TextView>(R.id.tvAdminOpenIncidents).text)
        assertEquals("1", activity.findViewById<TextView>(R.id.tvAdminPendingSchedules).text)
        assertEquals(
            "Central Eco Point · Active",
            activity.findViewById<TextView>(R.id.tvLatestCollectionPoint).text
        )
        assertEquals(
            "#1 · Overflowing container · Open",
            activity.findViewById<TextView>(R.id.tvLatestIncident).text
        )
        assertEquals(
            "#1 · Old sofa pickup · Pending",
            activity.findViewById<TextView>(R.id.tvLatestSchedule).text
        )
    }

    @Test
    fun `create and update actions send admin requests`() {
        val activity = Robolectric.buildActivity(AdminDashboardActivity::class.java).setup().get()
        TestFixtures.idleMainLooper()

        activity.findViewById<TextView>(R.id.etCollectionPointName).text = "North Point"
        activity.findViewById<TextView>(R.id.etCollectionPointDescription).text = "Near school"
        activity.findViewById<TextView>(R.id.etCollectionPointAddress).text = "Rua Norte"
        activity.findViewById<TextView>(R.id.etCollectionPointLatitude).text = "41.30"
        activity.findViewById<TextView>(R.id.etCollectionPointLongitude).text = "-8.71"
        activity.findViewById<Spinner>(R.id.spinnerCollectionPointType).setSelection(3)
        activity.findViewById<Button>(R.id.btnCreateCollectionPoint).performClick()
        TestFixtures.idleMainLooper()

        assertEquals("North Point", retrofitClientRule.fakeApiService.lastCreateCollectionPointRequest?.name)
        assertEquals("Near school", retrofitClientRule.fakeApiService.lastCreateCollectionPointRequest?.description)
        assertEquals("Rua Norte", retrofitClientRule.fakeApiService.lastCreateCollectionPointRequest?.address)
        assertEquals(41.30, retrofitClientRule.fakeApiService.lastCreateCollectionPointRequest?.latitude ?: 0.0, 0.0001)
        assertEquals(-8.71, retrofitClientRule.fakeApiService.lastCreateCollectionPointRequest?.longitude ?: 0.0, 0.0001)
        assertEquals(
            listOf("PAPER"),
            retrofitClientRule.fakeApiService.lastCreateCollectionPointRequest?.collectionTypes
        )

        activity.findViewById<TextView>(R.id.etIncidentId).text = "15"
        activity.findViewById<TextView>(R.id.etIncidentAdminNotes).text = "Done"
        activity.findViewById<Spinner>(R.id.spinnerIncidentStatus).setSelection(2)
        activity.findViewById<Button>(R.id.btnUpdateIncidentStatus).performClick()
        TestFixtures.idleMainLooper()

        assertEquals(15L, retrofitClientRule.fakeApiService.lastUpdatedIncidentId)
        assertEquals("RESOLVED", retrofitClientRule.fakeApiService.lastUpdatedIncidentStatus)
        assertEquals("Done", retrofitClientRule.fakeApiService.lastUpdatedIncidentNotes)

        activity.findViewById<TextView>(R.id.etScheduleId).text = "22"
        activity.findViewById<TextView>(R.id.etScheduleDate).text = "2026-03-01T08:00:00"
        activity.findViewById<TextView>(R.id.etScheduleAdminNotes).text = ""
        activity.findViewById<Spinner>(R.id.spinnerScheduleStatus).setSelection(2)
        activity.findViewById<Button>(R.id.btnUpdateScheduleStatus).performClick()
        TestFixtures.idleMainLooper()

        assertEquals(22L, retrofitClientRule.fakeApiService.lastUpdatedScheduleId)
        assertEquals("SCHEDULED", retrofitClientRule.fakeApiService.lastUpdatedScheduleStatus)
        assertEquals("2026-03-01T08:00:00", retrofitClientRule.fakeApiService.lastUpdatedScheduleDate)
        assertNull(retrofitClientRule.fakeApiService.lastUpdatedScheduleNotes)
    }

    @Test
    fun `shows empty dashboard placeholders and validates missing admin ids`() {
        retrofitClientRule.fakeApiService.allCollectionPointsResponse = retrofit2.Response.success(emptyList())
        retrofitClientRule.fakeApiService.allIncidentsResponse = retrofit2.Response.success(emptyList())
        retrofitClientRule.fakeApiService.allSchedulesResponse = retrofit2.Response.success(emptyList())
        val activity = Robolectric.buildActivity(AdminDashboardActivity::class.java).setup().get()
        TestFixtures.idleMainLooper()

        assertEquals("0", activity.findViewById<TextView>(R.id.tvAdminTotalPoints).text)
        assertEquals("0", activity.findViewById<TextView>(R.id.tvAdminOpenIncidents).text)
        assertEquals("0", activity.findViewById<TextView>(R.id.tvAdminPendingSchedules).text)
        assertEquals(
            activity.getString(R.string.admin_no_collection_points),
            activity.findViewById<TextView>(R.id.tvLatestCollectionPoint).text
        )

        activity.findViewById<Button>(R.id.btnUpdateIncidentStatus).performClick()
        TestFixtures.idleMainLooper()
        assertTrue(activity.findViewById<Button>(R.id.btnRefreshDashboard).isEnabled)

        activity.findViewById<Button>(R.id.btnUpdateScheduleStatus).performClick()
        TestFixtures.idleMainLooper()
        assertTrue(activity.findViewById<Button>(R.id.btnRefreshDashboard).isEnabled)
    }
}
