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

package me.kalicinski.sudoku.engine

import kotlinx.serialization.*
import me.kalicinski.sudoku.engine.SudokuBoard.Companion.BOARD_SIZE

@Serializable
class IntBoard() : SudokuBoard {

    //this used to be an IntArray, but we need a list for kotlinx.serialization
    val grid = MutableList(BOARD_SIZE) { 0 }

    constructor(initGrid: IntArray) : this() {
        initGrid.forEachIndexed { index, i ->
            grid[index] = i
        }
    }

    constructor(initGrid: List<Int>) : this(initGrid.toIntArray())

    override fun clearValues(pos: Int) {
        grid[pos] = 0
    }

    override fun isCommitedValue(pos: Int): Boolean {
        if (ENABLE_ASSERTIONS) {
            checkAssertions(pos, 1)
        }
        return grid[pos] and (1 shl COMMITED_VALUE_BIT_FIELD) != 0
    }

    override fun setCommitedValue(pos: Int, commited: Boolean): Unit {
        if (ENABLE_ASSERTIONS) {
            checkAssertions(pos, 1)
        }
        if (commited && possibleValuesNum(pos) != 1) {
            throw IllegalStateException("Can't commit when multiple values present")
        }

        if (commited) {
            grid[pos] = grid[pos] or (1 shl COMMITED_VALUE_BIT_FIELD)
        } else {
            grid[pos] = grid[pos] and (1 shl COMMITED_VALUE_BIT_FIELD).inv()
        }
    }

    override fun isStartingValue(pos: Int): Boolean {
        if (ENABLE_ASSERTIONS) {
            checkAssertions(pos, 1)
        }
        return grid[pos] and (1 shl STARTING_VALUE_BIT_FIELD) != 0
    }

    override fun setStartingValue(pos: Int, starting: Boolean) {
        if (ENABLE_ASSERTIONS) {
            checkAssertions(pos, 1)
        }
        if (starting && possibleValuesNum(pos) != 1) {
            throw IllegalStateException("Can't be a starting value when multiple values present")
        }
        if (starting) {
            grid[pos] = grid[pos] or (1 shl STARTING_VALUE_BIT_FIELD)
        } else {
            grid[pos] = grid[pos] and (1 shl STARTING_VALUE_BIT_FIELD).inv()
        }
    }

    override fun hasPossibleValue(pos: Int): Boolean {
        if (ENABLE_ASSERTIONS) {
            checkAssertions(pos, 1)
        }
        return (0..8).any { isValuePossible(pos, it + 1) }
    }

    override fun possibleValuesNum(pos: Int): Int {
        if (ENABLE_ASSERTIONS) {
            checkAssertions(pos, 1)
        }
        return (0..8).sumBy { grid[pos] shr it and 1 }
    }

    override fun possibleValues(pos: Int): List<Int> {
        if (ENABLE_ASSERTIONS) {
            checkAssertions(pos, 1)
        }
        return (1..9).filter { grid[pos] shr (it - 1) and 1 == 1 }.toList()
    }

    override fun setValue(pos: Int, value: Int, commited: Boolean) {
        if (ENABLE_ASSERTIONS) {
            checkAssertions(pos, value)
        }
        grid[pos] = 1 shl value - 1
        setCommitedValue(pos, commited)
    }

    override fun removePossibleValue(pos: Int, value: Int): Boolean {
        if (ENABLE_ASSERTIONS) {
            checkAssertions(pos, value)
        }
        val wasPossible = isValuePossible(pos, value)
        grid[pos] = grid[pos] and (1 shl value - 1).inv()
        if (wasPossible) {
            setCommitedValue(pos, false)
        }
        return wasPossible
    }

    override fun addPossibleValue(pos: Int, value: Int): Boolean {
        if (ENABLE_ASSERTIONS) {
            checkAssertions(pos, value)
        }
        val wasPossible = isValuePossible(pos, value)
        grid[pos] = grid[pos] or (1 shl value - 1)
        if (!wasPossible) {
            setCommitedValue(pos, false)
        }
        return !wasPossible
    }

    override fun isValuePossible(pos: Int, value: Int): Boolean {
        if (ENABLE_ASSERTIONS) {
            checkAssertions(pos, value)
        }
        return grid[pos] and (1 shl value - 1) != 0
    }

    override fun getFirstPossibleValue(pos: Int): Int {
        if (ENABLE_ASSERTIONS) {
            checkAssertions(pos, 1)
        }
        return (1..9).first { isValuePossible(pos, it) }
    }

    override fun copy(): IntBoard {
        return IntBoard(grid)
    }

    private fun checkAssertions(pos: Int, value: Int) {
        when {
            pos < 0 -> throw IllegalArgumentException("pos is " + pos)
            pos >= SudokuBoard.BOARD_SIZE -> throw IllegalArgumentException("pos is " + pos)
            value <= 0 -> throw IllegalArgumentException("val is " + value)
            value > 9 -> throw IllegalArgumentException("val is " + value)
        }
    }

    override fun toArray(): IntArray {
        return grid.toIntArray()
    }

    companion object {

        private val ENABLE_ASSERTIONS = true

        private val COMMITED_VALUE_BIT_FIELD = 9
        private val STARTING_VALUE_BIT_FIELD = 10

        fun fromArray(values: IntArray): SudokuBoard {
            if (values.size != SudokuBoard.BOARD_SIZE) {
                throw IllegalArgumentException("int[] values must be of length 81")
            }
            val board = IntBoard()
            for (i in values.indices) {
                if (values[i] != 0) {
                    board.addPossibleValue(i, values[i])
                } else {
                    for (j in 1..9) {
                        board.addPossibleValue(i, j)
                    }
                }
            }
            return board
        }
    }
}
