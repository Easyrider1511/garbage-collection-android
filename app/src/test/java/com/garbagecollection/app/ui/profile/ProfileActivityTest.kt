package com.garbagecollection.app.ui.profile

import android.widget.Button
import android.widget.TextView
import com.garbagecollection.app.R
import com.garbagecollection.app.model.UserDTO
import com.garbagecollection.app.testsupport.RetrofitClientRule
import com.garbagecollection.app.testsupport.TestFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ProfileActivityTest {

    @get:Rule
    val retrofitClientRule = RetrofitClientRule()

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
    }

    @Test
    fun `loadProfile populates editable fields from backend response`() {
        retrofitClientRule.fakeApiService.profileResponse = Response.success(
            UserDTO(
                id = 1L,
                username = "admin",
                email = "admin@example.com",
                fullName = "Admin User",
                role = "ADMIN",
                active = true,
                banned = false,
                phoneNumber = "910000000"
            )
        )

        val activity = Robolectric.buildActivity(ProfileActivity::class.java).setup().get()
        TestFixtures.idleMainLooper()

        assertEquals("Admin User", activity.findViewById<TextView>(R.id.etFullName).text.toString())
        assertEquals("admin@example.com", activity.findViewById<TextView>(R.id.etEmail).text.toString())
        assertEquals("910000000", activity.findViewById<TextView>(R.id.etPhone).text.toString())
    }

    @Test
    fun `saveProfile validates name and email before calling backend`() {
        val activity = Robolectric.buildActivity(ProfileActivity::class.java).setup().get()
        TestFixtures.idleMainLooper()

        activity.findViewById<TextView>(R.id.etFullName).text = ""
        activity.findViewById<TextView>(R.id.etEmail).text = ""
        activity.findViewById<Button>(R.id.btnSave).performClick()

        assertEquals(
            activity.getString(R.string.message_name_email_required),
            ShadowToast.getTextOfLatestToast()
        )
    }

    @Test
    fun `saveProfile sends update request and closes screen on success`() {
        val activity = Robolectric.buildActivity(ProfileActivity::class.java).setup().get()
        TestFixtures.idleMainLooper()

        activity.findViewById<TextView>(R.id.etFullName).text = "Updated User"
        activity.findViewById<TextView>(R.id.etEmail).text = "updated@example.com"
        activity.findViewById<TextView>(R.id.etPhone).text = ""
        activity.findViewById<TextView>(R.id.etNewPassword).text = "new-pass"
        activity.findViewById<Button>(R.id.btnSave).performClick()
        TestFixtures.idleMainLooper()

        assertEquals("Updated User", retrofitClientRule.fakeApiService.lastProfileUpdateRequest?.fullName)
        assertEquals("updated@example.com", retrofitClientRule.fakeApiService.lastProfileUpdateRequest?.email)
        assertEquals("new-pass", retrofitClientRule.fakeApiService.lastProfileUpdateRequest?.password)
        assertEquals(null, retrofitClientRule.fakeApiService.lastProfileUpdateRequest?.phoneNumber)
        assertTrue(activity.isFinishing)
    }
}
