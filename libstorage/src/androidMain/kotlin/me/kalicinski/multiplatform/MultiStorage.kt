package me.kalicinski.multiplatform

import android.content.SharedPreferences

actual class MultiStorage actual constructor() {

    private lateinit var prefs: SharedPreferences

    constructor(preferences: SharedPreferences) : this() {
        prefs = preferences
    }

    actual fun getString(key: String): String? {
        return prefs.getString(key, null)
    }

    actual fun putString(key: String, value: String?) {
        prefs.edit().putString(key, value).apply()
    }
}