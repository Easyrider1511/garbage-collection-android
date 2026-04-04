package com.garbagecollection.app.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("gc_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLE = "role"
        private const val KEY_USER_ID = "user_id"
    }

    fun saveSession(token: String, username: String, role: String, userId: Long) {
        prefs.edit {
            putString(KEY_TOKEN, token)
            putString(KEY_USERNAME, username)
            putString(KEY_ROLE, role)
            putLong(KEY_USER_ID, userId)
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun getRole(): String? = prefs.getString(KEY_ROLE, null)
    fun getUserId(): Long? {
        val id = prefs.getLong(KEY_USER_ID, -1)
        return if (id == -1L) null else id
    }

    fun isLoggedIn(): Boolean = getToken() != null
    fun clearSession() { prefs.edit { clear() } }
}
