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

import me.kalicinski.sudoku.engine.SudokuSolver.Board

class SudokuSolver(private val solvers: Array<Board.(Int, Boolean) -> SolverResult>) {

    fun solve(
            board: Board,
            maxSolutions: Int = 1,
            backtracks: Int = UNLIMITED_BACKTRACKING,
            progressListener: SolverListener? = null
    ): ArrayList<Board> {
        val solutions = ArrayList<Board>()
        val boardFlipped = board.copy()
        (0 until Board.BOARD_SIZE).filter { !boardFlipped.isCommitedValue(it) }.forEach {
            for (number in Board.NUMBERS) {
                boardFlipped.addPossibleValue(it, number)
            }
        }

        solveInternal(boardFlipped, solutions, maxSolutions, backtracks, progressListener)

        for (board1 in solutions) {
            (0 until Board.BOARD_SIZE).forEach { board1.setCommitedValue(it, true) }
        }
        return solutions
    }

    private fun solveInternal(
            board: Board,
            solutions: ArrayList<Board>,
            numOfSolutions: Int,
            backtracks: Int,
            progressListener: SolverListener?
    ) {
        var firstPass = true
        var madeChanges = false
        var unfilledCellFound = false

        var i = 0
        while (i < Board.BOARD_SIZE) {
            if (!board.hasPossibleValue(i)) {
                return
            }

            for (solver in solvers) {
                val result = solver(board, i, !firstPass)
                if (SolverResult.DISCARD_BOARD == result) {
                    return
                } else if (SolverResult.BOARD_CHANGED == result) {
                    madeChanges = true
                }

            }

            if (backtracks != 0 && !firstPass && board.possibleValuesNum(i) > 1) {
                val candidates = board.possibleValues(i).toMutableList()
                candidates.shuffle()
                for (candidate in candidates) {
                    val newBoard = board.copy()
                    newBoard.setValue(i, candidate, true)

                    solveInternal(
                            newBoard,
                            solutions,
                            numOfSolutions,
                            backtracks - 1,
                            progressListener
                    )
                    if (solutions.size == numOfSolutions) {
                        return
                    } else {
                        continue
                    }
                }
                return
            }

            if (madeChanges) {
                progressListener?.onBoardChanged(board)
            }

            if (board.possibleValuesNum(i) > 1) {
                unfilledCellFound = true
            }

            if (i == Board.BOARD_SIZE - 1) {
                if (firstPass) {
                    if (!madeChanges) {
                        firstPass = false
                    }
                    madeChanges = false
                    i = -1
                    unfilledCellFound = false
                } else {
                    if (!madeChanges && !unfilledCellFound) {
                        solutions.add(board)
                    }
                    return
                }
            }
            i++
        }
    }

    abstract class Board {

        open fun toArray(): IntArray {
            val board = IntArray(BOARD_SIZE)
            for (i in 0 until BOARD_SIZE) {
                if (possibleValuesNum(i) > 1) {
                    throw IllegalStateException("Cannot convert this board to int[], too "
                            + "many possible values")
                }
                board[i] = getFirstPossibleValue(i)
            }
            return board
        }

        /**
         * Returns true if the value in this cell is not a draft.
         * @param pos cell index
         * @return true if value is not a draft
         */
        abstract fun isCommitedValue(pos: Int): Boolean

        abstract fun setCommitedValue(pos: Int, commited: Boolean)

        /**
         * Returns true if the value in this cell is part of the sudoku board and can't be changed
         * @param pos cell index
         * @return true if value is a starting value of the sudoku board
         */
        abstract fun isStartingValue(pos: Int): Boolean

        abstract fun setStartingValue(pos: Int, starting: Boolean)

        /**
         * Checks if there are any numbers currently in this cell
         * @param pos cell index
         * @return true if the cell holds at least one value
         */
        abstract fun hasPossibleValue(pos: Int): Boolean

        /**
         * Checks how many numbers are currently in this cell
         * @param pos cell index
         * @return the number of currently held values
         */
        abstract fun possibleValuesNum(pos: Int): Int

        /**
         * Returns a list of current numbers in this cell
         * @param pos cell index
         * @return List containing all numbers in this cell
         */
        abstract fun possibleValues(pos: Int): List<Int>

        /**
         * Sets a single number in a cell, either as draft or not.
         * This call removes any other numbers from the cell.
         *
         * @param pos cell index
         * @param value number 1-9
         * @param commited true if the number is not a draft
         */
        abstract fun setValue(pos: Int, value: Int, commited: Boolean)

        /**
         * Unsets a number in a cell if it was set.
         * @param pos cell index
         * @param value number 1-9
         * @return true if the operation changed the board, false if it was no-op
         */
        abstract fun removePossibleValue(pos: Int, value: Int): Boolean

        /**
         * Sets a number in a cell if it was not set
         * @param pos cell index
         * @param value number 1-9
         * @return true if the operation changed the board, false if it was no-op
         */
        abstract fun addPossibleValue(pos: Int, value: Int): Boolean

        /**
         * Checks if the given number is currently set
         * @param pos cell index
         * @param value number 1-9 to check
         * @return true if the cell currently has the number set
         */
        abstract fun isValuePossible(pos: Int, value: Int): Boolean

        /**
         * Gets the first number that is set in this cell, if any
         * @param pos cell index
         * @return first number that is set, counting from 1-9
         */
        abstract fun getFirstPossibleValue(pos: Int): Int

        /**
         * Returns an deep copy of this board.
         * @return copy of this board
         */
        abstract fun copy(): Board

        companion object {
            const val BOARD_SIZE = 9 * 9
            val NUMBERS = (1..9).toList()

            inline fun getIndex(x: Int, y: Int) = y * 9 + x

            /**
             * Get field column from field index
             * @param i board cell index
             * @return 0-based column number for the given cell
             */
            inline fun getColumn(i: Int) = i % 9

            /**
             * Get field row from field index
             * @param i board cell index
             * @return 0-based row number for the given cell
             */
            inline fun getRow(i: Int) = i / 9

            /**
             * Get index of the first cell in the column with the given cell
             * @param i board cell index
             * @return index of first cell in the same column
             */
            inline fun getColumnStart(i: Int) = getIndex(i % 9, 0)

            /**
             * Get index of the first cell in the row with the given cell
             * @param i board cell index
             * @return index of first cell in the same row
             */
            inline fun getRowStart(i: Int) = getIndex(0, i / 9)

            /**
             * Get index of the first cell in the same 3x3 square as the given cell
             * @param i board cell index
             * @return index of first cell in the same square
             */
            inline fun getSquareStart(i: Int): Int {
                // has to be row: 0, 3, 6 and column: 0, 3, 6
                return getIndex(getColumn(i) / 3 * 3, getRow(i) / 3 * 3)
            }
        }

    }

    abstract class SolverListener {
        abstract fun onBoardChanged(board: Board)
    }

    companion object {
        const val UNLIMITED_BACKTRACKING = -1
        val defaultSolvers: Array<Board.(Int, Boolean) -> SolverResult> =
                arrayOf(::BasicConstraintSolver, ::OnlyOneSolver, ::SingleLineSolver)

        fun generate(listener: SolverListener? = null): Pair<Board, Board>? {
            val solver = SudokuSolver(defaultSolvers)
            val emptyBoard = IntBoard.fromArray(IntArray(Board.BOARD_SIZE))
            val solutions = solver.solve(emptyBoard, 1, UNLIMITED_BACKTRACKING, listener)
            if (!solutions.isEmpty()) {
                var startingBoard = solutions[0]
                val solvedBoard = startingBoard.copy()
                val removeList = 0.rangeTo(9 * 9 / 2 + 1).toMutableList()
                removeList.shuffle()
                var removed = 0

                while (!removeList.isEmpty()) {
                    val removeI = removeList.removeAt(0)
                    val tempBoard = startingBoard.copy()
                    for (i in 1..9) {
                        tempBoard.addPossibleValue(removeI, i)
                        tempBoard.addPossibleValue(80 - removeI, i)
                    }

                    val tempResults = solver.solve(
                            tempBoard,
                            2,
                            UNLIMITED_BACKTRACKING,
                            listener
                    )
                    if (tempResults.size == 1) {
                        listener?.onBoardChanged(tempBoard)
                        startingBoard = tempBoard
                        removed++
                        if (removed == Board.BOARD_SIZE - 18) {
                            break
                        }
                    }
                }

                for (i in 0 until Board.BOARD_SIZE) {
                    if (startingBoard.possibleValuesNum(i) == 1) {
                        startingBoard.setCommitedValue(i, true)
                        startingBoard.setStartingValue(i, true)
                        solvedBoard.setStartingValue(i, true)
                    } else {
                        startingBoard.setValue(i, 1, false)
                        startingBoard.removePossibleValue(i, 1)
                    }
                }

                return Pair(startingBoard, solvedBoard)
            }
            return null
        }
    }

    enum class SolverResult {
        NO_OP, BOARD_CHANGED, DISCARD_BOARD
    }
}

// SOLVERS BELOW

/**
 * Tries to solve the next step of the board, returning one of the results:
 * NO_OP - the solver didn't make any changes in this run
 * BOARD_CHANGED - the solver made changes to the board
 * DISCARD_BOARD - this board is unsolvable
 *
 * @param board a sudoku board
 * @param position cell index to start scanning for next solution
 * @param guess allow guessing and backtracking in the absence of certain moves
 * @return
 */

fun BasicConstraintSolver(
        board: Board,
        position: Int,
        guess: Boolean
): SudokuSolver.SolverResult {
    var madeChanges = false
    if (!guess && board.possibleValuesNum(position) == 1) {
        val value = board.getFirstPossibleValue(position)
        val rowStart = Board.getRowStart(position)
        for (x in 0..8) {
            if (rowStart + x == position) {
                continue
            }
            if (board.removePossibleValue(rowStart + x, value)) {
                madeChanges = true
            }
            if (!board.hasPossibleValue(rowStart + x)) {
                return SudokuSolver.SolverResult.DISCARD_BOARD
            }
        }
        val colStart = Board.getColumnStart(position)
        for (y in 0..8) {
            if (colStart + y * 9 == position) {
                continue
            }
            if (board.removePossibleValue(colStart + y * 9, value)) {
                madeChanges = true
            }
            if (!board.hasPossibleValue(colStart + y * 9)) {
                return SudokuSolver.SolverResult.DISCARD_BOARD
            }
        }
        val sqStart = Board.getSquareStart(position)
        for (x in 0..2) {
            for (y in 0..2) {
                if (sqStart + x + y * 9 == position) {
                    continue
                }
                if (board.removePossibleValue(sqStart + x + y * 9, value)) {
                    madeChanges = true
                }
                if (!board.hasPossibleValue(sqStart + x + y * 9)) {
                    return SudokuSolver.SolverResult.DISCARD_BOARD
                }
            }
        }
    }
    return if (madeChanges) {
        SudokuSolver.SolverResult.BOARD_CHANGED
    } else {
        SudokuSolver.SolverResult.NO_OP
    }
}

fun OnlyOneSolver(
        board: Board,
        position: Int,
        guess: Boolean
): SudokuSolver.SolverResult {
    if (guess || board.possibleValuesNum(position) < 2) {
        return SudokuSolver.SolverResult.NO_OP
    }

    var madeChanges = false
    for (candidate in board.possibleValues(position)) {
        var candidateExists = false
        val rowStart = Board.getRowStart(position)
        for (x in 0..8) {
            if (rowStart + x == position) {
                continue
            }
            if (board.isValuePossible(rowStart + x, candidate)) {
                candidateExists = true
                break
            }
        }
        if (!candidateExists) {
            board.setValue(position, candidate, true)
            madeChanges = true
            break
        } else {
            candidateExists = false
        }

        val colStart = Board.getColumnStart(position)
        for (y in 0..8) {
            if (colStart + y * 9 == position) {
                continue
            }
            if (board.isValuePossible(colStart + y * 9, candidate)) {
                candidateExists = true
                break
            }
        }

        if (!candidateExists) {
            board.setValue(position, candidate, true)
            madeChanges = true
            break
        } else {
            candidateExists = false
        }

        val sqStart = Board.getSquareStart(position)
        for (x in 0..2) {
            for (y in 0..2) {
                if (sqStart + x + y * 9 == position) {
                    continue
                }
                if (board.isValuePossible(sqStart + x + y * 9, candidate)) {
                    candidateExists = true
                    break
                }
            }
        }

        if (!candidateExists) {
            board.setValue(position, candidate, true)
            madeChanges = true
            break
        }
    }
    return if (madeChanges) {
        SudokuSolver.SolverResult.BOARD_CHANGED
    } else {
        SudokuSolver.SolverResult.NO_OP
    }
}

fun SingleLineSolver(
        board: Board,
        position: Int,
        guess: Boolean
): SudokuSolver.SolverResult {
    if (guess) {
        return SudokuSolver.SolverResult.NO_OP
    }
    var madeChanges = false
    val startX = Board.getColumn(position)
    val startY = Board.getRow(position)

    for (candidate in board.possibleValues(position)) {
        var sameX = true
        var sameY = true

        val sqStart = Board.getSquareStart(position)
        for (x in 0..2) {
            for (y in 0..2) {
                val curPosition = sqStart + x + y * 9
                if (curPosition == position) {
                    continue
                }
                if (board.isValuePossible(curPosition, candidate)) {
                    if (Board.getColumn(curPosition) != startX) {
                        sameX = false
                    }
                    if (Board.getRow(curPosition) != startY) {
                        sameY = false
                    }
                }
            }
        }
        if (sameX) {
            val rowStart = Board.getRowStart(position)
            for (x in 0..8) {
                val curPos = rowStart + x
                if (Board.getRow(curPos) / 3 == Board.getRow(position) / 3) {
                    continue
                }
                madeChanges = board.removePossibleValue(curPos, candidate)
            }
        }
        if (sameY) {
            val colStart = Board.getColumnStart(position)
            for (y in 0..8) {
                val curPos = colStart + y * 9
                if (Board.getColumn(curPos) / 3 == Board.getColumn(position) / 3) {
                    continue
                }
                madeChanges = board.removePossibleValue(curPos, candidate)

            }

        }
    }
    return if (madeChanges) {
        SudokuSolver.SolverResult.BOARD_CHANGED
    } else {
        SudokuSolver.SolverResult.NO_OP
    }
}

//currently broken and unused
fun OnlyPairInUnit(
        board: Board,
        position: Int,
        guess: Boolean
): SudokuSolver.SolverResult {
    if (guess) {
        return SudokuSolver.SolverResult.NO_OP
    }
    var madeChanges = false
    val startX = Board.getColumn(position)
    val startY = Board.getRow(position)
    val startSquare = Board.getSquareStart(position)

    for (candidate in board.possibleValues(position)) {
        var onlyInRow = true
        var onlyInColumn = true


        val rowStart = Board.getRowStart(position)
        for (x in 0..8) {
            if (Board.getSquareStart(rowStart + x) == startSquare) {
                continue
            }
            if (board.isValuePossible(rowStart + x, candidate)) {
                onlyInRow = false
                break
            }
        }
        if (onlyInRow) {
            for (x in 0..2) {
                for (y in 0..2) {
                    val curPosition = startSquare + x + y * 9
                    if (Board.getColumn(curPosition) == startX) {
                        continue
                    }
                    madeChanges = board.removePossibleValue(curPosition, candidate)
                }
            }
        }


        val colStart = Board.getColumnStart(position)
        for (y in 0..8) {
            if (Board.getSquareStart(colStart + y * 9) == startSquare) {
                continue
            }
            if (board.isValuePossible(colStart + y * 9, candidate)) {
                onlyInColumn = false
                break
            }
        }
        if (onlyInColumn) {
            for (x in 0..2) {
                for (y in 0..2) {
                    val curPosition = startSquare + x + y * 9
                    if (Board.getRow(curPosition) == startY) {
                        continue
                    }
                    madeChanges = board.removePossibleValue(curPosition, candidate)
                }
            }
        }
    }
    return if (madeChanges) {
        SudokuSolver.SolverResult.BOARD_CHANGED
    } else {
        SudokuSolver.SolverResult.NO_OP
    }
}