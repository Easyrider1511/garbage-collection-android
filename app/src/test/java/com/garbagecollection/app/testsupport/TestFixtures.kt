package com.garbagecollection.app.testsupport

import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.test.core.app.ApplicationProvider
import com.garbagecollection.app.util.SessionManager
import org.robolectric.Shadows.shadowOf

object TestFixtures {

    fun appContext(): Application =
        ApplicationProvider.getApplicationContext<Application>()

    fun clearAppState() {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
        val context = appContext()
        context.getSharedPreferences("gc_session", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        context.getSharedPreferences("gc_app_settings", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    fun saveAdminSession() {
        SessionManager(appContext()).saveSession(
            token = "test-token",
            username = "admin",
            role = "ADMIN",
            userId = 1L
        )
    }

    fun saveUserSession() {
        SessionManager(appContext()).saveSession(
            token = "user-token",
            username = "citizen",
            role = "USER",
            userId = 2L
        )
    }

    fun idleMainLooper() {
        shadowOf(Looper.getMainLooper()).idle()
    }

    fun setLastKnownLocation(
        provider: String = LocationManager.GPS_PROVIDER,
        latitude: Double = 41.15,
        longitude: Double = -8.61,
        timestamp: Long = 1_700_000_000_000L
    ) {
        val locationManager = appContext()
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = Location(provider).apply {
            this.latitude = latitude
            this.longitude = longitude
            time = timestamp
        }
        shadowOf(locationManager).simulateLocation(provider, location)
    }
}
