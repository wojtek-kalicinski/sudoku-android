package me.kalicinski.sudoku.datasource

import me.kalicinski.sudoku.engine.SudokuGame
import me.kalicinski.sudoku.engine.SudokuSolver
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class GenerateBoardSource @Inject constructor() {
    fun generateBoard(seed: Long): SudokuGame {
        val pair = SudokuSolver.generate(random = Random(seed))
        return SudokuGame(pair.first, seed).apply {
            solvedBoard = pair.second
        }
    }
}