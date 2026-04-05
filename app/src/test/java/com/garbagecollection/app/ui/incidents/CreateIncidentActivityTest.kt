package com.garbagecollection.app.ui.incidents

import android.Manifest
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.garbagecollection.app.R
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
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CreateIncidentActivityTest {

    @get:Rule
    val retrofitClientRule = RetrofitClientRule()

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
        shadowOf(TestFixtures.appContext()).grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        TestFixtures.setLastKnownLocation(latitude = 41.2, longitude = -8.62)
    }

    @Test
    fun `shows title error when incident title is missing`() {
        Robolectric.buildActivity(CreateIncidentActivity::class.java).useActivity { activity ->
            activity.findViewById<Button>(R.id.btnSubmit).performClick()

            assertEquals(
                activity.getString(R.string.message_required),
                activity.findViewById<TextView>(R.id.etTitle).error
            )
        }
    }

    @Test
    fun `submits incident with current location and selected enum values`() {
        Robolectric.buildActivity(CreateIncidentActivity::class.java).useActivity { activity ->
            activity.findViewById<TextView>(R.id.etTitle).text = "Broken bin"
            activity.findViewById<TextView>(R.id.etDescription).text = "Container lid is broken"
            activity.findViewById<TextView>(R.id.etAddress).text = "Rua do Teste"
            activity.findViewById<Spinner>(R.id.spinnerType).setSelection(3)
            activity.findViewById<Spinner>(R.id.spinnerSeverity).setSelection(2)
            activity.findViewById<Button>(R.id.btnSubmit).performClick()
            TestFixtures.idleMainLooper()

            val request = retrofitClientRule.fakeApiService.lastCreateIncidentRequest
            assertEquals("Broken bin", request?.title)
            assertEquals("Container lid is broken", request?.description)
            assertEquals(41.2, request?.latitude ?: 0.0, 0.0001)
            assertEquals(-8.62, request?.longitude ?: 0.0, 0.0001)
            assertEquals("Rua do Teste", request?.address)
            assertEquals("OVERFLOW", request?.type)
            assertEquals("HIGH", request?.severity)
            assertNull(request?.photoUrl)
            assertTrue(activity.isFinishing)
        }
    }
}
