package me.kalicinski.multiplatform

expect class MultiCanvas() {
    var color: Int
    var lineWidth: Float
    var textAlign: String
    var textSize: Float
    fun fillRect(x: Float, y: Float, width: Float, height: Float)
    fun drawRect(x: Float, y: Float, width: Float, height: Float)
    fun drawText(text: String, x: Float, y: Float)
    fun measureText(text: String, rect: Rect)
    fun drawLine(startX: Float, startY: Float, stopX: Float, stopY: Float)
}

expect class Rect() {
    var top: Int
    var bottom: Int
    var left: Int
    var right: Int

    fun width(): Int
    fun height(): Int
}