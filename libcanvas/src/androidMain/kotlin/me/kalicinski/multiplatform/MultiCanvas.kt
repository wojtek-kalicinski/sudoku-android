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

import android.graphics.Canvas
import android.graphics.Paint

actual class MultiCanvas actual constructor() {
    val paint = Paint()

    lateinit var canvas: Canvas

    constructor(canvas: Canvas) : this() {
        this.canvas = canvas
    }

    actual var color: Int
        get() = paint.color
        set(value) {
            paint.color = value
        }
    actual var lineWidth: Float
        get() = paint.strokeWidth
        set(value) {
            paint.strokeWidth = value
        }
    actual var textAlign: String
        get() = when (paint.textAlign) {
            Paint.Align.LEFT -> "left"
            Paint.Align.RIGHT -> "right"
            Paint.Align.CENTER -> "center"
            else -> "left"
        }
        set(value) {
            paint.textAlign = when (value) {
                "left" -> Paint.Align.LEFT
                "right" -> Paint.Align.RIGHT
                "center" -> Paint.Align.CENTER
                else -> Paint.Align.LEFT
            }
        }
    actual var textSize: Float
        get() = paint.textSize
        set(value) {
            paint.textSize = value
        }


    actual fun fillRect(x: Float, y: Float, width: Float, height: Float) {
        paint.style = Paint.Style.FILL
        canvas.drawRect(x, y, x + width, y + height, paint)
    }

    actual fun drawText(text: String, x: Float, y: Float) {
        paint.style = Paint.Style.FILL
        canvas.drawText(text, x, y, paint)
    }

    actual fun measureText(text: String, rect: Rect) {
        paint.getTextBounds(text, 0, text.length, rect)
    }

    actual fun drawLine(startX: Float, startY: Float, stopX: Float, stopY: Float) {
        paint.style = Paint.Style.STROKE
        canvas.drawLine(startX, startY, stopX, stopY, paint)
    }

    actual fun drawRect(x: Float, y: Float, width: Float, height: Float) {
        paint.style = Paint.Style.STROKE
        canvas.drawRect(x, y, x + width, y + height, paint)
    }
}

actual typealias Rect = android.graphics.Rect