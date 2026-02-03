package com.bountyhunter.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("BountyHunterPrefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
    }
    
    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    
    fun getAuthToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }
    
    fun getBearerToken(): String? {
        val token = getAuthToken()
        return if (token != null) "Bearer $token" else null
    }
    
    fun saveUser(id: Int, username: String, email: String) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, id)
            putString(KEY_USERNAME, username)
            putString(KEY_EMAIL, email)
            apply()
        }
    }
    
    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }
    
    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
