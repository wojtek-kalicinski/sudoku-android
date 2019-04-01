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

import kotlinx.cinterop.useContents
import platform.CoreGraphics.*
import platform.Foundation.NSString
import platform.Foundation.create
import platform.UIKit.*


actual class MultiCanvas actual constructor() {

    lateinit var canvas: CGContextRef
    fun grabContext(){
        canvas = UIGraphicsGetCurrentContext()!!
    }

    val paragraphStyle = NSMutableParagraphStyle()
    var font = UIFont.systemFontOfSize(10.0)

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
            CGContextSetRGBStrokeColor(
                    canvas,
                    r / 255.0,
                    g / 255.0,
                    b / 255.0,
                    a / 255.0
            )
            CGContextSetRGBFillColor(
                    canvas,
                    r / 255.0,
                    g / 255.0,
                    b / 255.0,
                    a / 255.0
            )
        }
    actual var lineWidth: Float = 1f
        get() = field
        set(value) {
            field = value
            CGContextSetLineWidth(canvas, value.toDouble())
        }
    actual var textAlign: String
        get() = when (paragraphStyle.alignment){
            NSTextAlignmentLeft -> "left"
            NSTextAlignmentRight -> "right"
            NSTextAlignmentCenter -> "center"
            else -> "left"
        }
        set(value) {
            paragraphStyle.setAlignment(when (value) {
                "left" -> NSTextAlignmentLeft
                "right" -> NSTextAlignmentRight
                "center" -> NSTextAlignmentCenter
                else -> NSTextAlignmentLeft
            })
        }

    actual var textSize: Float
        get() = font.pointSize.toFloat()
        set(value) {
            font = font.fontWithSize(value.toDouble())
        }

    actual fun fillRect(x: Float, y: Float, width: Float, height: Float) {
        CGContextFillRect(canvas, CGRectMake(
                x.toDouble(),
                y.toDouble(),
                width.toDouble(),
                height.toDouble()
        ))
    }

    actual fun drawText(text: String, x: Float, y: Float) {
        NSString.create(string = text).drawAtPoint(
                CGPointMake(x.toDouble(), y.toDouble()),
                withAttributes = mapOf(
                        NSParagraphStyleAttributeName to paragraphStyle,
                        NSFontAttributeName to font
                )
        )
    }

    actual fun measureText(text: String, rect: Rect) {
        NSString.create(string = text).sizeWithFont(font).useContents {
            rect.right = width.toInt()
            rect.bottom = height.toInt()
        }
        rect.left = 0
        rect.top = 0
    }

    actual fun drawLine(startX: Float, startY: Float, stopX: Float, stopY: Float) {
        CGContextBeginPath(canvas)
        CGContextMoveToPoint(canvas, startX.toDouble(), startY.toDouble())
        CGContextAddLineToPoint(canvas, stopX.toDouble(), stopY.toDouble())
        CGContextStrokePath(canvas)
    }

    actual fun drawRect(x: Float, y: Float, width: Float, height: Float) {
        CGContextStrokeRect(canvas, CGRectMake(
                x.toDouble(),
                y.toDouble(),
                width.toDouble(),
                height.toDouble()
        ))
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