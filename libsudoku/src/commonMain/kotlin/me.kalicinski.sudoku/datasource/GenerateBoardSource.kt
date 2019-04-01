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
package me.kalicinski.sudoku.datasource

import me.kalicinski.sudoku.engine.IntBoard
import me.kalicinski.sudoku.engine.SudokuGame
import me.kalicinski.sudoku.engine.SudokuSolver
import kotlin.random.Random

class GenerateBoardSource {
    fun generateBoard(seed: Long): SudokuGame {
        val pair = SudokuSolver.generate(random = Random(seed))
        return SudokuGame(pair.first as IntBoard, seed).apply {
            solvedBoard = pair.second as IntBoard
        }
    }
}