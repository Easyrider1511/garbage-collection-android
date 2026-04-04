package com.garbagecollection.app.ui.schedules

import android.Manifest
import android.widget.Button
import android.widget.TextView
import com.garbagecollection.app.R
import com.garbagecollection.app.testsupport.RetrofitClientRule
import com.garbagecollection.app.testsupport.TestFixtures
import com.garbagecollection.app.testsupport.useActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CreateScheduleActivityTest {

    @get:Rule
    val retrofitClientRule = RetrofitClientRule()

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
        shadowOf(TestFixtures.appContext()).grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        TestFixtures.setLastKnownLocation(latitude = 41.15, longitude = -8.61)
    }

    @Test
    fun `shows field error when description is missing`() {
        Robolectric.buildActivity(CreateScheduleActivity::class.java).useActivity { activity ->
            activity.findViewById<Button>(R.id.btnSubmit).performClick()

            assertEquals(
                activity.getString(R.string.message_required),
                activity.findViewById<TextView>(R.id.etDescription).error
            )
        }
    }

    @Test
    fun `submits pickup request with selected type and current location`() {
        Robolectric.buildActivity(CreateScheduleActivity::class.java).useActivity { activity ->
            activity.findViewById<TextView>(R.id.etDescription).text = "Collect old sofa"
            activity.findViewById<TextView>(R.id.etAddress).text = "Rua Central"
            activity.findViewById<Button>(R.id.btnSubmit).performClick()
            TestFixtures.idleMainLooper()

            val request = retrofitClientRule.fakeApiService.lastCreatePickupRequest
            assertEquals("Collect old sofa", request?.description)
            assertEquals("Furniture", request?.itemType)
            assertEquals(41.15, request?.latitude ?: 0.0, 0.0001)
            assertEquals(-8.61, request?.longitude ?: 0.0, 0.0001)
            assertEquals("Rua Central", request?.address)
            assertTrue(activity.isFinishing)
        }
    }
}
