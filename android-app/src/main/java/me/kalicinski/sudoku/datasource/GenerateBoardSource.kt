package me.kalicinski.sudoku.datasource

import me.kalicinski.sudoku.engine.SudokuBoard
import me.kalicinski.sudoku.engine.SudokuSolver
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenerateBoardSource @Inject constructor() : SudokuBoardSource {
    override var board: Pair<SudokuBoard, SudokuBoard?>?
        get() = SudokuSolver.generate()
        set(value) {
            throw IllegalStateException("Not supported")
        }
}