package com.garbagecollection.app.util

import com.garbagecollection.app.testsupport.TestFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SessionManagerTest {

    private lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
        sessionManager = SessionManager(TestFixtures.appContext())
    }

    @Test
    fun `starts logged out when no session is saved`() {
        assertFalse(sessionManager.isLoggedIn())
        assertNull(sessionManager.getToken())
        assertNull(sessionManager.getUsername())
        assertNull(sessionManager.getRole())
        assertNull(sessionManager.getUserId())
    }

    @Test
    fun `saveSession persists all session fields`() {
        sessionManager.saveSession(
            token = "token-123",
            username = "admin",
            role = "ADMIN",
            userId = 42L
        )

        assertTrue(sessionManager.isLoggedIn())
        assertEquals("token-123", sessionManager.getToken())
        assertEquals("admin", sessionManager.getUsername())
        assertEquals("ADMIN", sessionManager.getRole())
        assertEquals(42L, sessionManager.getUserId())
    }

    @Test
    fun `clearSession removes all stored values`() {
        sessionManager.saveSession("token-123", "admin", "ADMIN", 42L)

        sessionManager.clearSession()

        assertFalse(sessionManager.isLoggedIn())
        assertNull(sessionManager.getToken())
        assertNull(sessionManager.getUsername())
        assertNull(sessionManager.getRole())
        assertNull(sessionManager.getUserId())
    }
}
