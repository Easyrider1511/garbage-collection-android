package com.garbagecollection.app.ui

import android.Manifest
import android.widget.FrameLayout
import android.widget.TextView
import com.garbagecollection.app.R
import com.garbagecollection.app.testsupport.RetrofitClientRule
import com.garbagecollection.app.testsupport.TestFixtures
import com.garbagecollection.app.testsupport.useActivity
import com.garbagecollection.app.util.AppLanguageManager
import com.google.android.material.bottomnavigation.BottomNavigationView
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
class MainActivityTest {

    @get:Rule
    val retrofitClientRule = RetrofitClientRule()

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
        AppLanguageManager.applyLanguage(
            TestFixtures.appContext(),
            AppLanguageManager.LANGUAGE_EN
        )
        TestFixtures.saveAdminSession()
        shadowOf(TestFixtures.appContext()).grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        TestFixtures.setLastKnownLocation(latitude = 41.1, longitude = -8.6)
    }

    @Test
    fun `starts on the map tab and switches toolbar title when profile tab is selected`() {
        Robolectric.buildActivity(MainActivity::class.java).useActivity { activity ->
            TestFixtures.idleMainLooper()

            assertEquals(
                activity.getString(R.string.title_map),
                activity.supportActionBar?.title
            )
            assertTrue(activity.findViewById<FrameLayout>(R.id.fragment_container).childCount > 0)
            assertTrue(activity.findViewById<FrameLayout>(R.id.fragment_container).paddingBottom > 0)

            activity.findViewById<BottomNavigationView>(R.id.bottomNavigation).selectedItemId =
                R.id.nav_profile
            TestFixtures.idleMainLooper()

            assertEquals(
                activity.getString(R.string.title_profile),
                activity.supportActionBar?.title
            )
            assertEquals(
                "admin",
                activity.findViewById<TextView>(R.id.tvUsername).text
            )
        }
    }
}
