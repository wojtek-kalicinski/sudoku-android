package me.kalicinski.multiplatform

expect class MultiStorage() {
    fun getString(key: String): String?
    fun putString(key: String, value: String?)
}