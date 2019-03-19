package me.kalicinski.multiplatform

import org.w3c.dom.*
import kotlin.browser.localStorage

actual class MultiStorage actual constructor() {
    actual fun getString(key: String): String? {
        return localStorage.get(key)
    }

    actual fun putString(key: String, value: String?) {
        if (value != null) {
            localStorage.set(key, value)
        } else {
            localStorage.removeItem(key)
        }
    }
}