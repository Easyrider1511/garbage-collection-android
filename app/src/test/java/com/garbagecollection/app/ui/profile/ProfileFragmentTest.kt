package com.garbagecollection.app.ui.profile

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.garbagecollection.app.R
import com.garbagecollection.app.model.UserDTO
import com.garbagecollection.app.testsupport.FragmentTestActivity
import com.garbagecollection.app.testsupport.RetrofitClientRule
import com.garbagecollection.app.testsupport.TestFixtures
import com.garbagecollection.app.testsupport.useActivity
import com.garbagecollection.app.ui.admin.AdminDashboardActivity
import com.garbagecollection.app.ui.auth.LoginActivity
import com.garbagecollection.app.util.SessionManager
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
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ProfileFragmentTest {

    @get:Rule
    val retrofitClientRule = RetrofitClientRule()

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
        TestFixtures.saveAdminSession()
        retrofitClientRule.fakeApiService.profileResponse = Response.success(
            UserDTO(
                id = 1L,
                username = "admin",
                email = "admin@example.com",
                fullName = "System Administrator",
                role = "ADMIN",
                active = true,
                banned = false,
                phoneNumber = null
            )
        )
    }

    @Test
    fun `renders admin profile and opens profile and back-office screens`() {
        Robolectric.buildActivity(FragmentTestActivity::class.java).useActivity { activity ->
            val fragment = ProfileFragment()

            activity.supportFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment)
                .commitNow()
            TestFixtures.idleMainLooper()

            assertEquals("admin", activity.findViewById<TextView>(R.id.tvUsername).text)
            assertEquals("System Administrator", activity.findViewById<TextView>(R.id.tvFullName).text)
            assertEquals("admin@example.com", activity.findViewById<TextView>(R.id.tvEmail).text)
            assertEquals("Not set", activity.findViewById<TextView>(R.id.tvPhone).text)
            assertEquals("Administrator", activity.findViewById<TextView>(R.id.tvRole).text)
            assertEquals("Active", activity.findViewById<TextView>(R.id.tvStatus).text)
            assertEquals(View.VISIBLE, activity.findViewById<Button>(R.id.btnAdminDashboard).visibility)

            activity.findViewById<Button>(R.id.btnEditProfile).performClick()
            assertEquals(
                ProfileActivity::class.java.name,
                shadowOf(activity).nextStartedActivity.component?.className
            )

            activity.findViewById<Button>(R.id.btnAdminDashboard).performClick()
            assertEquals(
                AdminDashboardActivity::class.java.name,
                shadowOf(activity).nextStartedActivity.component?.className
            )
        }
    }

    @Test
    fun `logout clears the session and starts LoginActivity`() {
        Robolectric.buildActivity(FragmentTestActivity::class.java).useActivity { activity ->
            activity.supportFragmentManager.beginTransaction()
                .add(android.R.id.content, ProfileFragment())
                .commitNow()
            TestFixtures.idleMainLooper()

            activity.findViewById<Button>(R.id.btnLogout).performClick()

            assertEquals(
                LoginActivity::class.java.name,
                shadowOf(activity).nextStartedActivity.component?.className
            )
            assertTrue(SessionManager(TestFixtures.appContext()).isLoggedIn().not())
        }
    }

    @Test
    fun `hides the admin dashboard button for a standard user session`() {
        TestFixtures.clearAppState()
        TestFixtures.saveUserSession()
        retrofitClientRule.fakeApiService.profileResponse = Response.success(
            UserDTO(
                id = 2L,
                username = "citizen",
                email = "citizen@example.com",
                fullName = "Citizen User",
                role = "USER",
                active = false,
                banned = false,
                phoneNumber = "912345678"
            )
        )
        Robolectric.buildActivity(FragmentTestActivity::class.java).useActivity { activity ->
            activity.supportFragmentManager.beginTransaction()
                .add(android.R.id.content, ProfileFragment())
                .commitNow()
            TestFixtures.idleMainLooper()

            assertEquals(View.GONE, activity.findViewById<Button>(R.id.btnAdminDashboard).visibility)
            assertEquals("Inactive", activity.findViewById<TextView>(R.id.tvStatus).text)
        }
    }

    @Test
    fun `shows the admin dashboard button when backend role is ROLE_ADMIN even if session is stale`() {
        TestFixtures.clearAppState()
        SessionManager(TestFixtures.appContext()).saveSession(
            token = "stale-token",
            username = "admin",
            role = "USER",
            userId = 1L
        )
        retrofitClientRule.fakeApiService.profileResponse = Response.success(
            UserDTO(
                id = 1L,
                username = "admin",
                email = "admin@example.com",
                fullName = "System Administrator",
                role = "ROLE_ADMIN",
                active = true,
                banned = false,
                phoneNumber = null
            )
        )

        Robolectric.buildActivity(FragmentTestActivity::class.java).useActivity { activity ->
            activity.supportFragmentManager.beginTransaction()
                .add(android.R.id.content, ProfileFragment())
                .commitNow()
            TestFixtures.idleMainLooper()

            assertEquals(View.VISIBLE, activity.findViewById<Button>(R.id.btnAdminDashboard).visibility)
            assertEquals("Administrator", activity.findViewById<TextView>(R.id.tvRole).text)
            assertEquals("ROLE_ADMIN", SessionManager(TestFixtures.appContext()).getRole())
        }
    }
}
