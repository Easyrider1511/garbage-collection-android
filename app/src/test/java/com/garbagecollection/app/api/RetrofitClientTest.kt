package com.garbagecollection.app.api

import com.garbagecollection.app.testsupport.TestFixtures
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.garbagecollection.app.testsupport.RetrofitClientRule

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RetrofitClientTest {

    @get:Rule
    val retrofitClientRule = RetrofitClientRule()

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
    }

    @Test
    fun `getApiService returns the injected singleton instance`() {
        val context = TestFixtures.appContext()

        val firstInstance = RetrofitClient.getApiService(context)
        val secondInstance = RetrofitClient.getApiService(context)

        assertSame(retrofitClientRule.fakeApiService, firstInstance)
        assertSame(firstInstance, secondInstance)
    }

    @Test
    fun `getApiService builds and reuses a real Retrofit service when singleton is reset`() {
        val field = RetrofitClient::class.java.getDeclaredField("apiService")
        field.isAccessible = true
        field.set(RetrofitClient, null)
        val context = TestFixtures.appContext()

        val firstInstance = RetrofitClient.getApiService(context)
        val secondInstance = RetrofitClient.getApiService(context)

        assertNotSame(retrofitClientRule.fakeApiService, firstInstance)
        assertSame(firstInstance, secondInstance)
    }
}
