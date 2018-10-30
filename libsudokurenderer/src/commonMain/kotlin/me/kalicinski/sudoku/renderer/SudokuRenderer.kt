package me.kalicinski.sudoku.renderer

import me.kalicinski.multiplatform.MultiCanvas
import me.kalicinski.multiplatform.Rect
import kotlin.math.abs

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
    multiCanvas.textAlign = "center"
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
        multiCanvas.drawText(
                bigNumber,
                width / 2f,
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
            multiCanvas.drawText(
                    i.toString(),
                    x * width / 3f + width / 6f,
                    y * height / 3f + verticalShift + height / 6f
            )
        }
    }
}