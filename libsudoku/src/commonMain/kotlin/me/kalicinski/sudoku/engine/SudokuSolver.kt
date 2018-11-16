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

import kotlin.random.Random

class SudokuSolver(
        private val solvers: Array<SudokuBoard.(Int, Boolean) -> SolverResult>,
        private val random: Random? = null
) {

    fun solve(
            board: SudokuBoard,
            maxSolutions: Int = 1,
            backtracks: Int = UNLIMITED_BACKTRACKING,
            progressListener: SolverListener? = null
    ): ArrayList<SudokuBoard> {
        val solutions = ArrayList<SudokuBoard>()
        val boardFlipped = board.copy()
        (0 until SudokuBoard.BOARD_SIZE).filter { !boardFlipped.isCommitedValue(it) }.forEach {
            for (number in SudokuBoard.NUMBERS) {
                boardFlipped.addPossibleValue(it, number)
            }
        }

        solveInternal(boardFlipped, solutions, maxSolutions, backtracks, progressListener)

        for (board1 in solutions) {
            (0 until SudokuBoard.BOARD_SIZE).forEach { board1.setCommitedValue(it, true) }
        }
        return solutions
    }

    private fun solveInternal(
            board: SudokuBoard,
            solutions: ArrayList<SudokuBoard>,
            numOfSolutions: Int,
            backtracks: Int,
            progressListener: SolverListener?
    ) {
        var firstPass = true
        var madeChanges = false
        var unfilledCellFound = false

        var i = 0
        while (i < SudokuBoard.BOARD_SIZE) {
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
                if (random != null) {
                    candidates.shuffle(random)
                } else {
                    candidates.shuffle()
                }
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

            if (i == SudokuBoard.BOARD_SIZE - 1) {
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

    abstract class SolverListener {
        abstract fun onBoardChanged(board: SudokuBoard)
    }

    companion object {
        const val UNLIMITED_BACKTRACKING = -1
        val defaultSolvers: Array<SudokuBoard.(Int, Boolean) -> SolverResult> =
                arrayOf(::BasicConstraintSolver, ::OnlyOneSolver, ::SingleLineSolver)

        fun generate(
                listener: SolverListener? = null,
                random: Random? = null
        ): Pair<SudokuBoard, SudokuBoard> {
            val solver = SudokuSolver(defaultSolvers, random)
            val emptyBoard = IntBoard.fromArray(IntArray(SudokuBoard.BOARD_SIZE))
            val solutions = solver.solve(emptyBoard, 1, UNLIMITED_BACKTRACKING, listener)
            //starting from empty, solutions can't be empty
            var startingBoard = solutions[0]
            val solvedBoard = startingBoard.copy()
            val removeList = 0.rangeTo(9 * 9 / 2 + 1).toMutableList()
            if (random != null) {
                removeList.shuffle(random)
            } else {
                removeList.shuffle()
            }
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
                    if (removed == SudokuBoard.BOARD_SIZE - 18) {
                        break
                    }
                }
            }

            for (i in 0 until SudokuBoard.BOARD_SIZE) {
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
        board: SudokuBoard,
        position: Int,
        guess: Boolean
): SudokuSolver.SolverResult {
    var madeChanges = false
    if (!guess && board.possibleValuesNum(position) == 1) {
        val value = board.getFirstPossibleValue(position)
        val rowStart = SudokuBoard.getRowStart(position)
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
        val colStart = SudokuBoard.getColumnStart(position)
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
        val sqStart = SudokuBoard.getSquareStart(position)
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
        board: SudokuBoard,
        position: Int,
        guess: Boolean
): SudokuSolver.SolverResult {
    if (guess || board.possibleValuesNum(position) < 2) {
        return SudokuSolver.SolverResult.NO_OP
    }

    var madeChanges = false
    for (candidate in board.possibleValues(position)) {
        var candidateExists = false
        val rowStart = SudokuBoard.getRowStart(position)
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

        val colStart = SudokuBoard.getColumnStart(position)
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

        val sqStart = SudokuBoard.getSquareStart(position)
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
        board: SudokuBoard,
        position: Int,
        guess: Boolean
): SudokuSolver.SolverResult {
    if (guess) {
        return SudokuSolver.SolverResult.NO_OP
    }
    var madeChanges = false
    val startX = SudokuBoard.getColumn(position)
    val startY = SudokuBoard.getRow(position)

    for (candidate in board.possibleValues(position)) {
        var sameX = true
        var sameY = true

        val sqStart = SudokuBoard.getSquareStart(position)
        for (x in 0..2) {
            for (y in 0..2) {
                val curPosition = sqStart + x + y * 9
                if (curPosition == position) {
                    continue
                }
                if (board.isValuePossible(curPosition, candidate)) {
                    if (SudokuBoard.getColumn(curPosition) != startX) {
                        sameX = false
                    }
                    if (SudokuBoard.getRow(curPosition) != startY) {
                        sameY = false
                    }
                }
            }
        }
        if (sameX) {
            val rowStart = SudokuBoard.getRowStart(position)
            for (x in 0..8) {
                val curPos = rowStart + x
                if (SudokuBoard.getRow(curPos) / 3 == SudokuBoard.getRow(position) / 3) {
                    continue
                }
                madeChanges = board.removePossibleValue(curPos, candidate)
            }
        }
        if (sameY) {
            val colStart = SudokuBoard.getColumnStart(position)
            for (y in 0..8) {
                val curPos = colStart + y * 9
                if (SudokuBoard.getColumn(curPos) / 3 == SudokuBoard.getColumn(position) / 3) {
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
        board: SudokuBoard,
        position: Int,
        guess: Boolean
): SudokuSolver.SolverResult {
    if (guess) {
        return SudokuSolver.SolverResult.NO_OP
    }
    var madeChanges = false
    val startX = SudokuBoard.getColumn(position)
    val startY = SudokuBoard.getRow(position)
    val startSquare = SudokuBoard.getSquareStart(position)

    for (candidate in board.possibleValues(position)) {
        var onlyInRow = true
        var onlyInColumn = true


        val rowStart = SudokuBoard.getRowStart(position)
        for (x in 0..8) {
            if (SudokuBoard.getSquareStart(rowStart + x) == startSquare) {
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
                    if (SudokuBoard.getColumn(curPosition) == startX) {
                        continue
                    }
                    madeChanges = board.removePossibleValue(curPosition, candidate)
                }
            }
        }


        val colStart = SudokuBoard.getColumnStart(position)
        for (y in 0..8) {
            if (SudokuBoard.getSquareStart(colStart + y * 9) == startSquare) {
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
                    if (SudokuBoard.getRow(curPosition) == startY) {
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