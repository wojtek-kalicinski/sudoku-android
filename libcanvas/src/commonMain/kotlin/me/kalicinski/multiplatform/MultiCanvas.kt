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