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

    // keep track if user already logged in (even when app destroyed)
    fun isLoggedIn() = preferences.getBoolean(LOGGED_IN, false)

    fun setLoggedIn(status: Boolean) {
        editor.putBoolean(LOGGED_IN, status).apply()
        editor.apply()
    }

    // keep track of which mode the user uses
    fun getUserMode() = preferences.getString(USER_MODE, "tenant")
    fun setUserMode(mode: String) {
        // mode = ["tenant", "landlord", "roommate"] <- possible values
        editor.putString(USER_MODE, mode).apply()
        editor.apply()
    }

    // keep track of where the "LoginFragment" was invoked/redirected to
    // possible context = "Mode selection", "Account", "Favorite", "Message"
    fun getRedirectContext() = preferences.getString(REDIRECT_FROM, "Mode selection")
    fun setRedirectContext(context: String) {
        editor.putString(REDIRECT_FROM, context).apply()
        editor.apply()
    }

    companion object {
        private const val PREFERENCE_NAME = "loginData"
        private const val FIRST_LOGIN = "isFirstLogin"
        private const val BIOMETRIC_ENABLED = "biometricEnabled"
        private const val LOGGED_IN = "isLoggedIn"
        private const val USER_MODE = "userMode"
        private const val REDIRECT_FROM = "redirectContext"
    }
}