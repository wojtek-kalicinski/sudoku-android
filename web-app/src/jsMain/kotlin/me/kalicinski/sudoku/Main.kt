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

package me.kalicinski.sudoku

import me.kalicinski.multiplatform.MultiCanvas
import me.kalicinski.sudoku.engine.SudokuSolver
import me.kalicinski.sudoku.engine.SudokuBoard
import me.kalicinski.sudoku.renderer.drawBoard
import me.kalicinski.sudoku.renderer.drawCell
import org.uncommons.maths.random.MersenneTwisterRNG
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

lateinit var canvas: MultiCanvas
lateinit var canvasEl: HTMLCanvasElement
var board: SudokuBoard? = null
var solvedBoard: SudokuBoard? = null

fun init(){
    println("init")
    canvasEl = document.getElementById("board") as HTMLCanvasElement
    val canvasCtx: CanvasRenderingContext2D = canvasEl.getContext("2d") as CanvasRenderingContext2D
    canvas = MultiCanvas(canvasCtx)
    generate()
}

fun main(args: Array<String>) {
    window.onload = { init() }
}

fun generate() {
    val seed = window.location.pathname.substring(
            window.location.pathname.lastIndexOf('/') + 1
    ).toLongOrNull()
    val boardPair = seed?.let {
        SudokuSolver.generate(random = MersenneTwisterRNG(it))
    } ?: SudokuSolver.generate()
    solvedBoard = boardPair.second
    board = boardPair.first
    redraw()
}

fun solve() {
    board = solvedBoard
    redraw()
}

fun redraw() {
    //clear
    canvas.color = 0xFFFFFFFF.toInt() //white
    canvas.fillRect(
            0f,
            0f,
            canvasEl.width.toFloat(),
            canvasEl.height.toFloat()
    )
    drawBoard(
            canvas,
            canvasEl.width.toFloat(),
            canvasEl.height.toFloat(),
            2f,
            1f,
            0xFF212121.toInt(),
            0xFF757575.toInt()
    )

    val cellWidth = canvasEl.width.toFloat() / 9
    val cellHeight = canvasEl.height.toFloat() / 9

    for (i in 0 until SudokuBoard.BOARD_SIZE){
        canvas.canvas.save()
        canvas.canvas.translate(
                (SudokuBoard.getColumn(i) * cellWidth).toDouble(),
                (SudokuBoard.getRow(i) * cellHeight).toDouble()
        )
        drawCell(
                canvas,
                board?.possibleValues(i)?.toSet() ?: emptySet(),
                cellWidth,
                cellHeight,
                false,
                board?.isStartingValue(i) == false,
                false,
                board?.isCommitedValue(i) == true,
                0xFFe6e6e6.toInt(),
                0xFF777777.toInt(),
                0xFFBB0000.toInt(),
                0xFFFFECB3.toInt(),
                0.8f * cellHeight,
                0.2f * cellHeight
        )
        canvas.canvas.restore()
    }
}
