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

import kotlinx.html.dom.create
import kotlinx.html.id
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.tr
import me.kalicinski.sudoku.engine.SudokuSolver
import me.kalicinski.sudoku.engine.SudokuBoard
import org.uncommons.maths.random.MersenneTwisterRNG
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    var i = 0;
    document.body!!.append(document.create.table {
        (0..8).forEach {
            tr {
                (0..8).forEach {
                    td {
                        id = "cell${i++}"
                    }
                }
            }
        }
    })
}

var solvedBoard: SudokuBoard? = null

fun generate() {
    val seed = window.location.pathname.substring(
            window.location.pathname.lastIndexOf('/') + 1
    ).toLongOrNull()
    val boardPair = seed?.let {
        SudokuSolver.generate(random = MersenneTwisterRNG(it))!!
    } ?: SudokuSolver.generate()!!
    solvedBoard = boardPair.second
    val board = boardPair.first

    (0 until SudokuBoard.BOARD_SIZE).forEach {
        document.getElementById("cell${it}")!!.textContent = if (board.hasPossibleValue(it)){
            board.getFirstPossibleValue(it).toString()
        } else {
            ""
        }
    }
}

fun solve() {
    (0 until SudokuBoard.BOARD_SIZE).forEach {
        document.getElementById("cell${it}")!!.textContent =
                solvedBoard?.getFirstPossibleValue(it).toString()
    }
}