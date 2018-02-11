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

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class IntBoardTest {
    private lateinit var board: SudokuBoard

    @BeforeTest
    fun setUp() {
        board = IntBoard()
    }

    @Test
    fun hasPossibleValue() {
        board.setValue(5, 2, false)
        assertTrue(board.hasPossibleValue(5))

        board.addPossibleValue(5, 5)
        assertTrue(board.hasPossibleValue(5))
        assertFalse(board.hasPossibleValue(4))
    }

    @Test

    fun possibleValuesNum() {
        assertEquals(board.possibleValuesNum(2).toLong(), 0)

        board.addPossibleValue(2, 1)
        assertEquals(board.possibleValuesNum(2).toLong(), 1)

        board.addPossibleValue(2, 3)
        assertEquals(board.possibleValuesNum(2).toLong(), 2)

        board.addPossibleValue(2, 1)
        assertEquals(board.possibleValuesNum(2).toLong(), 2)
        assertEquals(board.possibleValuesNum(3).toLong(), 0)
        assertNotEquals(board.possibleValuesNum(2).toLong(), 0)
    }

    @Test
    fun possibleValues() {
        val list = mutableListOf<Int>(1, 9)

        board.setValue(2, 1, false)
        board.addPossibleValue(2, 1)
        board.addPossibleValue(2, 9)

        var boardList = board.possibleValues(2)
        assertTrue(boardList.toTypedArray() contentEquals list.toTypedArray())

        board.addPossibleValue(2, 9)
        boardList = board.possibleValues(2)
        assertTrue(boardList.toTypedArray() contentEquals list.toTypedArray())

        board.addPossibleValue(2, 8)
        boardList = board.possibleValues(2)
        assertFalse(boardList.toTypedArray() contentEquals list.toTypedArray())
    }

    @Test
    fun setValue() {
        board.setValue(80, 9, false)
        assertTrue(board.isValuePossible(80, 9))

    }

    @Test
    fun removePossibleValue() {
        board.setValue(5, 9, false)
        board.addPossibleValue(5, 5)
        assertTrue(board.isValuePossible(5, 9))
        assertTrue(board.isValuePossible(5, 5))

        board.removePossibleValue(5, 9)
        assertTrue(board.isValuePossible(5, 5))
        assertFalse(board.isValuePossible(5, 9))

        board.removePossibleValue(5, 5)
        assertFalse(board.isValuePossible(5, 5))
        assertFalse(board.isValuePossible(5, 9))
    }

    @Test
    fun addPossibleValue() {
        board.addPossibleValue(5, 5)
        assertTrue(board.isValuePossible(5, 5))

        board.addPossibleValue(5, 9)
        assertTrue(board.isValuePossible(5, 5))
        assertTrue(board.isValuePossible(5, 9))

    }

    @Test
    fun isValuePossible() {
        board.setValue(5, 9, false)
        board.addPossibleValue(5, 5)
        assertTrue(board.isValuePossible(5, 9))
        assertTrue(board.isValuePossible(5, 5))

        board.removePossibleValue(5, 9)
        assertTrue(board.isValuePossible(5, 5))
        assertFalse(board.isValuePossible(5, 9))

        board.removePossibleValue(5, 5)
        assertFalse(board.isValuePossible(5, 5))
        assertFalse(board.isValuePossible(5, 9))
        assertFalse(board.isValuePossible(6, 9))
    }

    @Test
    fun getNextPossibleValue() {
        board.setValue(5, 9, false)
        assertEquals(board.getFirstPossibleValue(5).toLong(), 9)

        board.addPossibleValue(5, 5)
        assertEquals(board.getFirstPossibleValue(5).toLong(), 5)
    }

}