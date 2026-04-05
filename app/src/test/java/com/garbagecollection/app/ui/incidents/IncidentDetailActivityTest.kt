package com.garbagecollection.app.ui.incidents

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.garbagecollection.app.R
import com.garbagecollection.app.testsupport.FakeApiService
import com.garbagecollection.app.testsupport.RetrofitClientRule
import com.garbagecollection.app.testsupport.TestFixtures
import com.garbagecollection.app.testsupport.useActivity
import com.garbagecollection.app.util.IncidentPhotoManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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
class IncidentDetailActivityTest {

    @get:Rule
    val retrofitClientRule = RetrofitClientRule()

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
    }

    @Test
    fun `finishes immediately when incident id extra is missing`() {
        Robolectric.buildActivity(IncidentDetailActivity::class.java).useActivity { activity ->
            assertTrue(activity.isFinishing)
        }
    }

    @Test
    fun `loads incident details and reports same incident`() {
        val photoUri = IncidentPhotoManager.createPhotoUri(TestFixtures.appContext()).toString()
        retrofitClientRule.fakeApiService.incidentResponse = retrofit2.Response.success(
            FakeApiService.sampleIncident(photoUrl = photoUri)
        )
        val intent = Intent(TestFixtures.appContext(), IncidentDetailActivity::class.java)
            .putExtra("incident_id", 5L)

        Robolectric.buildActivity(IncidentDetailActivity::class.java, intent).useActivity { activity ->
            TestFixtures.idleMainLooper()

            assertEquals("Overflowing container", activity.findViewById<TextView>(R.id.tvTitle).text)
            assertEquals(
                "Paper container is full",
                activity.findViewById<TextView>(R.id.tvDescription).text
            )
            assertEquals("Overflow", activity.findViewById<TextView>(R.id.tvType).text)
            assertEquals("High", activity.findViewById<TextView>(R.id.tvSeverity).text)
            assertEquals("Open", activity.findViewById<TextView>(R.id.tvStatus).text)
            assertEquals(
                "Lat: 39.8228, Lng: -7.4931",
                activity.findViewById<TextView>(R.id.tvLocation).text
            )
            assertEquals("Main Street", activity.findViewById<TextView>(R.id.tvAddress).text)
            assertEquals("2 reports", activity.findViewById<TextView>(R.id.tvReportCount).text)
            assertEquals("2026-01-15T10:00:00", activity.findViewById<TextView>(R.id.tvCreatedAt).text)
            assertEquals("Review scheduled", activity.findViewById<TextView>(R.id.tvAdminNotes).text)
            assertNotEquals(View.GONE, activity.findViewById<TextView>(R.id.tvPhotoLabel).visibility)

            activity.findViewById<Button>(R.id.btnReportSame).performClick()
            TestFixtures.idleMainLooper()

            assertEquals(5L, retrofitClientRule.fakeApiService.lastReportIncidentId)
        }
    }

    @Test
    fun `hides photo views when incident has no photo`() {
        retrofitClientRule.fakeApiService.incidentResponse = retrofit2.Response.success(
            FakeApiService.sampleIncident(photoUrl = null)
        )
        val intent = Intent(TestFixtures.appContext(), IncidentDetailActivity::class.java)
            .putExtra("incident_id", 2L)

        Robolectric.buildActivity(IncidentDetailActivity::class.java, intent).useActivity { activity ->
            TestFixtures.idleMainLooper()

            assertEquals(View.GONE, activity.findViewById<TextView>(R.id.tvPhotoLabel).visibility)
        }
    }
}
