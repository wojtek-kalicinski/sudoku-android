package me.kalicinski.multiplatform

import platform.Foundation.NSUserDefaults

actual class MultiStorage actual constructor() {
    private val defaults: NSUserDefaults

    init {
        defaults = NSUserDefaults.standardUserDefaults()
    }

    actual fun getString(key: String): String? {
        return defaults.stringForKey(key)
    }

    actual fun putString(key: String, value: String?) {
        defaults.setObject(value, key)
    }
}