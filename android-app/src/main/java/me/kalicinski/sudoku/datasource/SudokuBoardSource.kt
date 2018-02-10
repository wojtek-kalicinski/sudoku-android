package me.kalicinski.sudoku.datasource

import me.kalicinski.sudoku.engine.SudokuBoard

interface SudokuBoardSource {
    /**
     * Contains the current state of the board and the solved board for checking answers
     */
    var board: Pair<SudokuBoard,SudokuBoard?>?
}
