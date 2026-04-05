package com.garbagecollection.app.ui.admin

import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.garbagecollection.app.R
import com.garbagecollection.app.testsupport.FakeApiService
import com.garbagecollection.app.testsupport.RetrofitClientRule
import com.garbagecollection.app.testsupport.TestFixtures
import com.garbagecollection.app.testsupport.useActivity
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
        Robolectric.buildActivity(AdminDashboardActivity::class.java).useActivity { activity ->
            TestFixtures.idleMainLooper()

            assertEquals("2", activity.findViewById<TextView>(R.id.tvAdminTotalPoints).text)
            assertEquals("1", activity.findViewById<TextView>(R.id.tvAdminOpenIncidents).text)
            assertEquals("1", activity.findViewById<TextView>(R.id.tvAdminPendingSchedules).text)
            assertEquals(
                "Central Eco Point · Full",
                activity.findViewById<TextView>(R.id.tvLatestCollectionPoint).text
            )
            assertEquals(
                "#2 · Overflowing container · Resolved",
                activity.findViewById<TextView>(R.id.tvLatestIncident).text
            )
            assertEquals(
                "#2 · Old sofa pickup · Completed",
                activity.findViewById<TextView>(R.id.tvLatestSchedule).text
            )
            assertEquals(
                "#2 · Overflowing container · Resolved",
                activity.findViewById<Spinner>(R.id.spinnerIncidentTarget)
                    .getItemAtPosition(0).toString()
            )
            assertEquals(
                "Type: Overflow\nSeverity: High\nAddress: Main Street\nReports: 2\nNotes: Review scheduled",
                activity.findViewById<TextView>(R.id.tvSelectedIncidentDetails).text
            )
            assertEquals(
                "#2 · Old sofa pickup · Completed",
                activity.findViewById<Spinner>(R.id.spinnerScheduleTarget)
                    .getItemAtPosition(0).toString()
            )
            assertEquals(
                "Item: Furniture\nAddress: Main Street\nScheduled date: 2026-01-20T09:00:00\nNotes: Assigned to route 2",
                activity.findViewById<TextView>(R.id.tvSelectedScheduleDetails).text
            )
        }
    }

    @Test
    fun `create and update actions send admin requests`() {
        Robolectric.buildActivity(AdminDashboardActivity::class.java).useActivity { activity ->
            TestFixtures.idleMainLooper()

            activity.findViewById<TextView>(R.id.etCollectionPointName).text = "North Point"
            activity.findViewById<TextView>(R.id.etCollectionPointDescription).text = "Near school"
            activity.findViewById<TextView>(R.id.etCollectionPointAddress).text = "Rua Norte"
            activity.findViewById<TextView>(R.id.etCollectionPointLatitude).text = "41.30"
            activity.findViewById<TextView>(R.id.etCollectionPointLongitude).text = "-8.71"
            activity.findViewById<Spinner>(R.id.spinnerCollectionPointType).setSelection(3)
            activity.findViewById<Button>(R.id.btnCreateCollectionPoint).performClick()
            TestFixtures.idleMainLooper()

            assertEquals(
                "North Point",
                retrofitClientRule.fakeApiService.lastCreateCollectionPointRequest?.name
            )
            assertEquals(
                "Near school",
                retrofitClientRule.fakeApiService.lastCreateCollectionPointRequest?.description
            )
            assertEquals(
                "Rua Norte",
                retrofitClientRule.fakeApiService.lastCreateCollectionPointRequest?.address
            )
            assertEquals(
                41.30,
                retrofitClientRule.fakeApiService.lastCreateCollectionPointRequest?.latitude ?: 0.0,
                0.0001
            )
            assertEquals(
                -8.71,
                retrofitClientRule.fakeApiService.lastCreateCollectionPointRequest?.longitude ?: 0.0,
                0.0001
            )
            assertEquals(
                listOf("PAPER"),
                retrofitClientRule.fakeApiService.lastCreateCollectionPointRequest?.collectionTypes
            )

            activity.findViewById<Spinner>(R.id.spinnerIncidentTarget).setSelection(0)
            activity.findViewById<TextView>(R.id.etIncidentAdminNotes).text = "Done"
            activity.findViewById<Spinner>(R.id.spinnerIncidentStatus).setSelection(2)
            activity.findViewById<Button>(R.id.btnUpdateIncidentStatus).performClick()
            TestFixtures.idleMainLooper()

            assertEquals(2L, retrofitClientRule.fakeApiService.lastUpdatedIncidentId)
            assertEquals("RESOLVED", retrofitClientRule.fakeApiService.lastUpdatedIncidentStatus)
            assertEquals("Done", retrofitClientRule.fakeApiService.lastUpdatedIncidentNotes)

            activity.findViewById<Spinner>(R.id.spinnerScheduleTarget).setSelection(0)
            activity.findViewById<TextView>(R.id.etScheduleDate).text = "2026-03-01T08:00:00"
            activity.findViewById<TextView>(R.id.etScheduleAdminNotes).text = ""
            activity.findViewById<Spinner>(R.id.spinnerScheduleStatus).setSelection(2)
            activity.findViewById<Button>(R.id.btnUpdateScheduleStatus).performClick()
            TestFixtures.idleMainLooper()

            assertEquals(2L, retrofitClientRule.fakeApiService.lastUpdatedScheduleId)
            assertEquals("SCHEDULED", retrofitClientRule.fakeApiService.lastUpdatedScheduleStatus)
            assertEquals("2026-03-01T08:00:00", retrofitClientRule.fakeApiService.lastUpdatedScheduleDate)
            assertNull(retrofitClientRule.fakeApiService.lastUpdatedScheduleNotes)
        }
    }

    @Test
    fun `shows empty dashboard placeholders and validates missing admin ids`() {
        retrofitClientRule.fakeApiService.allCollectionPointsResponse = retrofit2.Response.success(emptyList())
        retrofitClientRule.fakeApiService.allIncidentsResponse = retrofit2.Response.success(emptyList())
        retrofitClientRule.fakeApiService.allSchedulesResponse = retrofit2.Response.success(emptyList())
        Robolectric.buildActivity(AdminDashboardActivity::class.java).useActivity { activity ->
            TestFixtures.idleMainLooper()

            assertEquals("0", activity.findViewById<TextView>(R.id.tvAdminTotalPoints).text)
            assertEquals("0", activity.findViewById<TextView>(R.id.tvAdminOpenIncidents).text)
            assertEquals("0", activity.findViewById<TextView>(R.id.tvAdminPendingSchedules).text)
            assertEquals(
                activity.getString(R.string.admin_no_collection_points),
                activity.findViewById<TextView>(R.id.tvLatestCollectionPoint).text
            )
            assertEquals(
                activity.getString(R.string.admin_no_incidents),
                activity.findViewById<Spinner>(R.id.spinnerIncidentTarget).selectedItem.toString()
            )
            assertEquals(
                activity.getString(R.string.admin_no_schedules),
                activity.findViewById<Spinner>(R.id.spinnerScheduleTarget).selectedItem.toString()
            )
            assertTrue(activity.findViewById<Spinner>(R.id.spinnerIncidentTarget).isEnabled.not())
            assertTrue(activity.findViewById<Spinner>(R.id.spinnerScheduleTarget).isEnabled.not())

            activity.findViewById<Button>(R.id.btnUpdateIncidentStatus).performClick()
            TestFixtures.idleMainLooper()
            assertTrue(activity.findViewById<Button>(R.id.btnUpdateIncidentStatus).isEnabled.not())

            activity.findViewById<Button>(R.id.btnUpdateScheduleStatus).performClick()
            TestFixtures.idleMainLooper()
            assertTrue(activity.findViewById<Button>(R.id.btnUpdateScheduleStatus).isEnabled.not())
        }
    }
}
