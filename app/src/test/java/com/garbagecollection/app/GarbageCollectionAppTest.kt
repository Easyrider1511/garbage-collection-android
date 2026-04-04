package com.garbagecollection.app

import com.garbagecollection.app.testsupport.TestFixtures
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GarbageCollectionAppTest {

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
    }

    @Test
    fun `application context is created and app onCreate executes`() {
        val app = TestFixtures.appContext()

        assertTrue(app is GarbageCollectionApp)
    }
}
