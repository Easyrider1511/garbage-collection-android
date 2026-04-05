package com.garbagecollection.app.ui.schedules

import android.view.View
import android.widget.TextView
import com.garbagecollection.app.R
import com.garbagecollection.app.testsupport.FragmentTestActivity
import com.garbagecollection.app.testsupport.RetrofitClientRule
import com.garbagecollection.app.testsupport.TestFixtures
import com.garbagecollection.app.testsupport.useActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SchedulesFragmentTest {

    @get:Rule
    val retrofitClientRule = RetrofitClientRule()

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
        TestFixtures.saveUserSession()
    }

    @Test
    fun `shows empty state when there are no schedules and opens create screen from FAB`() {
        retrofitClientRule.fakeApiService.mySchedulesResponse = Response.success(emptyList())
        Robolectric.buildActivity(FragmentTestActivity::class.java).useActivity { activity ->
            val fragment = SchedulesFragment()

            activity.supportFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment)
                .commitNow()
            TestFixtures.idleMainLooper()

            assertEquals(2L, retrofitClientRule.fakeApiService.lastRequestedUserId)
            assertEquals(
                View.VISIBLE,
                activity.findViewById<TextView>(R.id.tvEmpty).visibility
            )

            activity.findViewById<FloatingActionButton>(R.id.fabCreateSchedule).performClick()

            val startedIntent = shadowOf(activity).nextStartedActivity
            assertNotNull(startedIntent)
            assertTrue(
                startedIntent.component?.className
                    ?.contains(CreateScheduleActivity::class.java.simpleName) == true
            )
        }
    }
}
