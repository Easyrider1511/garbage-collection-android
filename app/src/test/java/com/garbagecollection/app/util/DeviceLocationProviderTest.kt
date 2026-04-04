package com.garbagecollection.app.util

import android.Manifest
import android.location.LocationManager
import com.garbagecollection.app.testsupport.TestFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DeviceLocationProviderTest {

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
        shadowOf(TestFixtures.appContext()).denyPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    @Test
    fun `hasLocationPermission returns false and getLastKnownLocation returns null without permission`() {
        val context = TestFixtures.appContext()

        assertFalse(DeviceLocationProvider.hasLocationPermission(context))
        assertNull(DeviceLocationProvider.getLastKnownLocation(context))
    }

    @Test
    fun `getLastKnownLocation returns the newest available provider location`() {
        val context = TestFixtures.appContext()
        shadowOf(context).grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        TestFixtures.setLastKnownLocation(
            provider = LocationManager.NETWORK_PROVIDER,
            latitude = 40.0,
            longitude = -8.0,
            timestamp = 1000L
        )
        TestFixtures.setLastKnownLocation(
            provider = LocationManager.GPS_PROVIDER,
            latitude = 41.15,
            longitude = -8.61,
            timestamp = 2000L
        )

        val location = DeviceLocationProvider.getLastKnownLocation(context)

        assertTrue(DeviceLocationProvider.hasLocationPermission(context))
        assertEquals(41.15, location?.latitude ?: 0.0, 0.0001)
        assertEquals(-8.61, location?.longitude ?: 0.0, 0.0001)
    }
}
