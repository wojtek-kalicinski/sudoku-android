/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package me.kalicinski.multiplatform

import org.w3c.dom.*

actual class MultiCanvas actual constructor() {

    lateinit var canvas: CanvasRenderingContext2D

    constructor(canvas: CanvasRenderingContext2D) : this() {
        this.canvas = canvas
    }

    actual var color: Int = 0
        get() {
            return field
        }
        set(value) {
            field = value
            val a = value shr 24 and 0xFF
            val r = value shr 16 and 0xFF
            val g = value shr 8 and 0xFF
            val b = value and 0xFF
            canvas.fillStyle = "rgba($r,$g,$b,$a)"
            canvas.strokeStyle = "rgba($r,$g,$b,$a)"
        }
    actual var lineWidth: Float
        get() = canvas.lineWidth.toFloat()
        set(value) {
            canvas.lineWidth = value.toDouble()
        }
    actual var textAlign: String
        get() = canvas.textAlign.toString()
        set(value) {
            canvas.textAlign = when (value) {
                "left" -> CanvasTextAlign.LEFT
                "right" -> CanvasTextAlign.RIGHT
                "center" -> CanvasTextAlign.CENTER
                else -> CanvasTextAlign.LEFT
            }
        }

    actual var textSize: Float = 10F
        get() = field
        set(value) {
            field = value
            canvas.font = value.toInt().toString() + "px sans-serif"
        }

    actual fun fillRect(x: Float, y: Float, width: Float, height: Float) {
        canvas.fillRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
    }

    actual fun drawText(text: String, x: Float, y: Float) {
        canvas.fillText(text, x.toDouble(), y.toDouble())
    }

    actual fun measureText(text: String, rect: Rect) {
        rect.right = canvas.measureText(text).width.toInt()
        rect.left = 0
        rect.top = 0
        rect.bottom = 0
    }

    actual fun drawLine(startX: Float, startY: Float, stopX: Float, stopY: Float) {
        canvas.beginPath()
        canvas.moveTo(startX.toDouble(), startY.toDouble())
        canvas.lineTo(stopX.toDouble(), stopY.toDouble())
        canvas.stroke()
    }

    actual fun drawRect(x: Float, y: Float, width: Float, height: Float) {
        canvas.strokeRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
    }

}

actual class Rect actual constructor() {
    actual var top: Int = 0
    actual var bottom: Int = 0
    actual var left: Int = 0
    actual var right: Int = 0

    actual fun width(): Int = right - left
    actual fun height(): Int = bottom - top
}