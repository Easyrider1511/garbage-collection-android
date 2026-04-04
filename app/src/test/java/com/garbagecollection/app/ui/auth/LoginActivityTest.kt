package com.garbagecollection.app.ui.auth

import android.widget.Button
import android.widget.TextView
import com.garbagecollection.app.R
import com.garbagecollection.app.model.AuthResponse
import com.garbagecollection.app.testsupport.RetrofitClientRule
import com.garbagecollection.app.testsupport.TestFixtures
import com.garbagecollection.app.ui.MainActivity
import com.garbagecollection.app.util.AppLanguageManager
import com.garbagecollection.app.util.SessionManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
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
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LoginActivityTest {

    @get:Rule
    val retrofitClientRule = RetrofitClientRule()

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
    }

    @Test
    fun `auto login navigates directly to MainActivity when a session already exists`() {
        TestFixtures.saveAdminSession()

        val activity = Robolectric.buildActivity(LoginActivity::class.java).setup().get()

        assertTrue(activity.isFinishing)
        assertEquals(
            MainActivity::class.java.name,
            shadowOf(activity).nextStartedActivity.component?.className
        )
    }

    @Test
    fun `shows validation toast when username or password are empty`() {
        val activity = Robolectric.buildActivity(LoginActivity::class.java).setup().get()

        activity.findViewById<Button>(R.id.btnLogin).performClick()

        assertEquals(
            activity.getString(R.string.message_fill_all_fields),
            ShadowToast.getTextOfLatestToast()
        )
    }

    @Test
    fun `language toggle stores Portuguese Portugal`() {
        val activity = Robolectric.buildActivity(LoginActivity::class.java).setup().get()

        activity.findViewById<Button>(R.id.btnLanguagePortuguese).performClick()

        assertEquals(
            AppLanguageManager.LANGUAGE_PT_PT,
            AppLanguageManager.getSavedLanguageTag(TestFixtures.appContext())
        )
    }

    @Test
    fun `successful login saves session and navigates to MainActivity`() {
        retrofitClientRule.fakeApiService.loginResponse = Response.success(
            AuthResponse("auth-token", "citizen", "USER", 99L)
        )
        val activity = Robolectric.buildActivity(LoginActivity::class.java).setup().get()

        activity.findViewById<TextView>(R.id.etUsername).text = "citizen"
        activity.findViewById<TextView>(R.id.etPassword).text = "secret123"
        activity.findViewById<Button>(R.id.btnLogin).performClick()
        TestFixtures.idleMainLooper()

        val sessionManager = SessionManager(TestFixtures.appContext())
        assertEquals("citizen", retrofitClientRule.fakeApiService.lastLoginRequest?.username)
        assertEquals("secret123", retrofitClientRule.fakeApiService.lastLoginRequest?.password)
        assertEquals("auth-token", sessionManager.getToken())
        assertEquals("citizen", sessionManager.getUsername())
        assertEquals("USER", sessionManager.getRole())
        assertEquals(99L, sessionManager.getUserId())
        assertTrue(activity.findViewById<Button>(R.id.btnLogin).isEnabled)
        assertEquals(
            MainActivity::class.java.name,
            shadowOf(activity).nextStartedActivity.component?.className
        )
    }

    @Test
    fun `shows invalid credentials toast when backend rejects login`() {
        retrofitClientRule.fakeApiService.loginResponse = Response.error(
            401,
            "invalid".toResponseBody("text/plain".toMediaType())
        )
        val activity = Robolectric.buildActivity(LoginActivity::class.java).setup().get()

        activity.findViewById<TextView>(R.id.etUsername).text = "citizen"
        activity.findViewById<TextView>(R.id.etPassword).text = "wrong-password"
        activity.findViewById<Button>(R.id.btnLogin).performClick()
        TestFixtures.idleMainLooper()

        assertEquals(
            activity.getString(R.string.message_invalid_credentials),
            ShadowToast.getTextOfLatestToast()
        )
    }

    @Test
    fun `register link opens RegisterActivity`() {
        val activity = Robolectric.buildActivity(LoginActivity::class.java).setup().get()

        activity.findViewById<TextView>(R.id.tvRegister).performClick()

        assertEquals(
            RegisterActivity::class.java.name,
            shadowOf(activity).nextStartedActivity.component?.className
        )
    }
}
