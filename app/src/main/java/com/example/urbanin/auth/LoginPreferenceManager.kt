package com.example.urbanin.auth

import android.content.Context
import android.content.SharedPreferences

class LoginPreferenceManager(context: Context) {

    private val preferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        preferences = context.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        editor = preferences.edit()
    }

    // check for first login
    fun isFirstLogin() = preferences.getBoolean(FIRST_LOGIN, true)
    fun setFirstLogin() {
        editor.putBoolean(FIRST_LOGIN, false).commit()
        editor.commit()
    }

    // write login details
    fun writeLoginCreds(email: String, pwd: String) {
        with(editor) {
            putString("email", email)
            putString("password", pwd)
            apply()
        }
    }

    // get login details
    fun fetchLoginCreds(): Pair<String?, String?> {
        return Pair(
            preferences.getString("email", ""),
            preferences.getString("password", "")
        )
    }

    // biometric opt-in
    fun isBiometricEnabled() = preferences.getBoolean(BIOMETRIC_ENABLED, false)
    fun setBiometricEnabled(status: Boolean) {
        editor.putBoolean(BIOMETRIC_ENABLED, status).commit()
        editor.commit()
    }

    companion object {
        private const val PREFERENCE_NAME = "loginData"
        private const val FIRST_LOGIN = "isFirstLogin"
        private const val BIOMETRIC_ENABLED = "biometricEnabled"
    }
}