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
package me.kalicinski.sudoku.renderer

import me.kalicinski.multiplatform.MultiCanvas
import me.kalicinski.multiplatform.Rect

fun drawBoard(
        multiCanvas: MultiCanvas,
        width: Float,
        height: Float,
        strokeWidthBold: Float,
        strokeWidthNormal: Float,
        colorBold: Int,
        colorNormal: Int
){
    for (i in 0..9) {
        if (i % 3 == 0) {
            multiCanvas.lineWidth = strokeWidthBold
            multiCanvas.color = colorBold
            if (i in 1..8) {
                multiCanvas.drawLine(i * width / 9f, 0f, i * width / 9f, height)
            }
            multiCanvas.drawLine(0f, i * height / 9f, width, i * height / 9f)
        } else {
            multiCanvas.lineWidth = strokeWidthNormal
            multiCanvas.color = colorNormal
            if (i in 1..8) {
                multiCanvas.drawLine(i * width / 9f, 0f, i * width / 9f, height)
            }
            multiCanvas.drawLine(0f, i * height / 9f, width, i * height / 9f)
        }
    }
}

val textBounds = Rect()

fun drawCell(
        multiCanvas: MultiCanvas,
        numbersShowing: Set<Int>,
        width: Float,
        height: Float,
        isFocused: Boolean,
        isChangeable: Boolean,
        isNumberIncorrect: Boolean,
        isNumberConfirmed: Boolean,
        colorFocused: Int,
        colorChangeable: Int,
        colorIncorrect: Int,
        colorHighlight: Int,
        bigNumberHeight: Float,
        smallNumberHeight: Float
){
    multiCanvas.textAlign = "left"
    if (isFocused) {
        multiCanvas.color = colorFocused
        multiCanvas.fillRect(0f, 0f, width, height)
    }

    multiCanvas.color = if (isChangeable) {
        colorChangeable
    } else {
        0xFF000000.toInt() //Color.BLACK
    }

    if (isNumberIncorrect) {
        multiCanvas.color = colorIncorrect
    }

    if (isNumberConfirmed) {
        multiCanvas.textSize = bigNumberHeight
        val bigNumber = numbersShowing.iterator().next().toString()
        multiCanvas.measureText(bigNumber, textBounds)

        val verticalShift: Float = if (textBounds.height() != 0){
            -textBounds.bottom + textBounds.height() / 2.0f
        } else {
            multiCanvas.textSize / 3
        }

        val horizontalShift: Float = if (textBounds.width() != 0){
            -textBounds.left + textBounds.width() / 2.0f
        } else 0f

        multiCanvas.drawText(
                bigNumber,
                width / 2f - horizontalShift,
                height / 2f + verticalShift
        )
    } else {
        for (i in numbersShowing) {
            val x = (i - 1) % 3
            val y = (i - 1) / 3
            val numberHighlighted = 0
            if (numberHighlighted == i) {
                val color = multiCanvas.color
                multiCanvas.color = colorHighlight
                multiCanvas.drawRect(
                        x * width / 3f,
                        y * height / 3f,
                        (x + 1) * width / 3f,
                        (y + 1) * height / 3f
                )
                multiCanvas.color = color
            }

            multiCanvas.textSize = smallNumberHeight
            multiCanvas.measureText(i.toString(), textBounds)
            val verticalShift: Float = if (textBounds.height() != 0){
                textBounds.bottom + textBounds.height() / 2.0f
            } else {
                multiCanvas.textSize / 3
            }

            val horizontalShift: Float = if (textBounds.width() != 0){
                -textBounds.left + textBounds.width() / 2.0f
            } else 0f

            multiCanvas.drawText(
                    i.toString(),
                    x * width / 3f + width / 6f - horizontalShift,
                    y * height / 3f + verticalShift + height / 6f
            )
        }
    }
}