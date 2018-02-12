package me.kalicinski.sudoku.datasource

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.content.edit
import com.google.gson.Gson
import me.kalicinski.sudoku.engine.IntBoard
import me.kalicinski.sudoku.engine.SudokuBoard
import me.kalicinski.sudoku.engine.SudokuSolver
import javax.inject.Inject

private const val PREF_BOARD = "PREF_BOARD"

class LocalBoardSource @Inject constructor(context: Context) {

    private val appContext: Context = context.applicationContext
    private val gson = Gson()

    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    var board: Pair<SudokuBoard, SudokuBoard?>?
        get() {
            return sharedPreferences.getString(PREF_BOARD,null)?.let {
                val loadedBoard = gson.fromJson(it, IntBoard::class.java)

                //need to recreate the solution
                //let's start with an empty starting board
                val startingBoard = loadedBoard.copy()
                for (i in 0 until SudokuBoard.BOARD_SIZE) {
                    with(startingBoard) {
                        if (!isStartingValue(i)) {
                            clearValues(i)
                        }
                    }
                }
                //and calculate the solved board
                val solutions = SudokuSolver(SudokuSolver.defaultSolvers).solve(startingBoard)
                when {
                    solutions.isNotEmpty() ->
                        Pair<SudokuBoard, SudokuBoard>(loadedBoard, solutions[0])
                    else -> null
                }
            }
        }
        set(value) {
            sharedPreferences.edit {
                if (value != null) {
                    putString(
                            PREF_BOARD,
                            gson.toJson(value.first)
                    )
                } else {
                    remove(PREF_BOARD)
                }
            }
        }
}