package me.kalicinski.sudoku.datasource

import me.kalicinski.sudoku.engine.SudokuBoard
import me.kalicinski.sudoku.engine.SudokuGame
import me.kalicinski.sudoku.engine.SudokuSolver
import org.uncommons.maths.random.MersenneTwisterRNG
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenerateBoardSource @Inject constructor() {
    fun generateBoard(seed: Long): SudokuGame {
        val pair = SudokuSolver.generate(random = MersenneTwisterRNG(seed))
        return SudokuGame(pair.first, seed).apply {
            solvedBoard = pair.second
        }
    }
}