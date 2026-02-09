package com.example.wellminder.data.manager

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("wellminder_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_ONBOARDING_COMPLETE = "is_onboarding_complete"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_GENDER = "gender"
        private const val KEY_WEIGHT = "weight"
        private const val KEY_HEIGHT = "height"
        private const val KEY_AGE = "age"
    }

    private val _userIdFlow = kotlinx.coroutines.flow.MutableStateFlow(userId)
    val userIdFlow: kotlinx.coroutines.flow.StateFlow<Long> = _userIdFlow

    var userId: Long
        get() = sharedPreferences.getLong(KEY_USER_ID, -1L)
        set(value) {
            sharedPreferences.edit().putLong(KEY_USER_ID, value).apply()
            _userIdFlow.value = value
        }

    var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var isOnboardingComplete: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_ONBOARDING_COMPLETE, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_ONBOARDING_COMPLETE, value).apply()

    var userName: String?
        get() = sharedPreferences.getString(KEY_USER_NAME, null)
        set(value) = sharedPreferences.edit().putString(KEY_USER_NAME, value).apply()

    var gender: String?
        get() = sharedPreferences.getString(KEY_GENDER, null)
        set(value) = sharedPreferences.edit().putString(KEY_GENDER, value).apply()

    var weight: Float
        get() = sharedPreferences.getFloat(KEY_WEIGHT, 0f)
        set(value) = sharedPreferences.edit().putFloat(KEY_WEIGHT, value).apply()

    var height: Float
        get() = sharedPreferences.getFloat(KEY_HEIGHT, 0f)
        set(value) = sharedPreferences.edit().putFloat(KEY_HEIGHT, value).apply()

    var age: Int
        get() = sharedPreferences.getInt(KEY_AGE, 0)
        set(value) = sharedPreferences.edit().putInt(KEY_AGE, value).apply()

    fun clear() {
        sharedPreferences.edit().clear().apply()
        _userIdFlow.value = -1L
    }
}
