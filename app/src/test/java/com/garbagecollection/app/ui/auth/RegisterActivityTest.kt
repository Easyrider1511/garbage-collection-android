package com.garbagecollection.app.ui.auth

import android.widget.Button
import android.widget.TextView
import com.garbagecollection.app.R
import com.garbagecollection.app.model.AuthResponse
import com.garbagecollection.app.testsupport.RetrofitClientRule
import com.garbagecollection.app.testsupport.TestFixtures
import com.garbagecollection.app.ui.MainActivity
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
import org.robolectric.shadows.ShadowToast
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RegisterActivityTest {

    @get:Rule
    val retrofitClientRule = RetrofitClientRule()

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
    }

    @Test
    fun `shows required fields validation when form is empty`() {
        val activity = Robolectric.buildActivity(RegisterActivity::class.java).setup().get()

        activity.findViewById<Button>(R.id.btnRegister).performClick()

        assertEquals(
            activity.getString(R.string.message_fill_required_fields),
            ShadowToast.getTextOfLatestToast()
        )
    }

    @Test
    fun `rejects short passwords before calling backend`() {
        val activity = Robolectric.buildActivity(RegisterActivity::class.java).setup().get()

        activity.findViewById<TextView>(R.id.etUsername).text = "citizen"
        activity.findViewById<TextView>(R.id.etEmail).text = "citizen@example.com"
        activity.findViewById<TextView>(R.id.etPassword).text = "123"
        activity.findViewById<TextView>(R.id.etFullName).text = "Citizen User"
        activity.findViewById<Button>(R.id.btnRegister).performClick()

        assertEquals(
            activity.getString(R.string.message_password_min_length),
            ShadowToast.getTextOfLatestToast()
        )
    }

    @Test
    fun `successful registration saves session and launches MainActivity`() {
        retrofitClientRule.fakeApiService.registerResponse = Response.success(
            AuthResponse("register-token", "citizen", "USER", 77L)
        )
        val activity = Robolectric.buildActivity(RegisterActivity::class.java).setup().get()

        activity.findViewById<TextView>(R.id.etUsername).text = "citizen"
        activity.findViewById<TextView>(R.id.etEmail).text = "citizen@example.com"
        activity.findViewById<TextView>(R.id.etPassword).text = "secret123"
        activity.findViewById<TextView>(R.id.etFullName).text = "Citizen User"
        activity.findViewById<TextView>(R.id.etPhone).text = "912345678"
        activity.findViewById<Button>(R.id.btnRegister).performClick()
        TestFixtures.idleMainLooper()

        val sessionManager = SessionManager(TestFixtures.appContext())
        assertEquals("citizen", retrofitClientRule.fakeApiService.lastRegisterRequest?.username)
        assertEquals("citizen@example.com", retrofitClientRule.fakeApiService.lastRegisterRequest?.email)
        assertEquals("912345678", retrofitClientRule.fakeApiService.lastRegisterRequest?.phoneNumber)
        assertEquals("register-token", sessionManager.getToken())
        assertEquals(
            MainActivity::class.java.name,
            shadowOf(activity).nextStartedActivity.component?.className
        )
        assertTrue(activity.isFinishing)
    }

    @Test
    fun `shows registration failed toast when backend returns an error response`() {
        retrofitClientRule.fakeApiService.registerResponse = Response.error(
            400,
            "invalid".toResponseBody("text/plain".toMediaType())
        )
        val activity = Robolectric.buildActivity(RegisterActivity::class.java).setup().get()

        activity.findViewById<TextView>(R.id.etUsername).text = "citizen"
        activity.findViewById<TextView>(R.id.etEmail).text = "citizen@example.com"
        activity.findViewById<TextView>(R.id.etPassword).text = "secret123"
        activity.findViewById<TextView>(R.id.etFullName).text = "Citizen User"
        activity.findViewById<Button>(R.id.btnRegister).performClick()
        TestFixtures.idleMainLooper()

        assertEquals(
            activity.getString(R.string.message_registration_failed),
            ShadowToast.getTextOfLatestToast()
        )
    }

    @Test
    fun `login link closes the registration screen`() {
        val activity = Robolectric.buildActivity(RegisterActivity::class.java).setup().get()

        activity.findViewById<TextView>(R.id.tvLogin).performClick()

        assertTrue(activity.isFinishing)
    }
}
