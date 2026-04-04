package com.garbagecollection.app.util

import com.garbagecollection.app.testsupport.TestFixtures
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class IncidentPhotoManagerTest {

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
    }

    @Test
    fun `createPhotoUri creates app file directory and a FileProvider URI`() {
        val context = TestFixtures.appContext()

        val photoUri = IncidentPhotoManager.createPhotoUri(context)

        assertTrue(photoUri.scheme == "content" || photoUri.scheme == "file")
        if (photoUri.scheme == "content") {
            assertEquals("${context.packageName}.fileprovider", photoUri.authority)
        }
        assertTrue(File(context.filesDir, "incident_photos").isDirectory)
        assertTrue(photoUri.toString().contains("incident_photos"))
        assertTrue(photoUri.toString().endsWith(".jpg"))
    }
}
