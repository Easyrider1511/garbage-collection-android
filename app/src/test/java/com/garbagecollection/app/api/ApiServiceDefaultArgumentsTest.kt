package com.garbagecollection.app.api

import com.garbagecollection.app.testsupport.FakeApiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiServiceDefaultArgumentsTest {

    @Test
    fun `default arguments delegate to the fake implementation`() = runBlocking {
        val apiService: ApiService = FakeApiService()

        assertTrue(apiService.getNearbyCollectionPoints(39.0, -8.0).isSuccessful)
        assertTrue(apiService.updateIncidentStatus(1L, "RESOLVED").isSuccessful)
        assertTrue(apiService.updateScheduleStatus(1L, "SCHEDULED").isSuccessful)
    }
}
