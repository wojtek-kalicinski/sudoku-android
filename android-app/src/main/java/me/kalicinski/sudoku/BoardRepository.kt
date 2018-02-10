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

package me.kalicinski.sudoku

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.content.edit
import com.google.gson.Gson
import me.kalicinski.sudoku.engine.IntBoard
import me.kalicinski.sudoku.engine.SudokuSolver
import me.kalicinski.sudoku.engine.SudokuSolver.Board
import org.json.JSONException
import java.util.concurrent.Executors

class BoardRepository(context: Context) {

    private val appContext: Context
    private val gson = Gson()

    init {
        appContext = context.applicationContext
    }

    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    private val executor = Executors.newSingleThreadExecutor()

    fun getBoard(regen: Boolean): LiveData<Pair<Board, Board>> {
        val liveData = MutableLiveData<Pair<Board, Board>>()
        executor.execute {
            //contains logic that reads the board from SharedPreferences
            //or generates a fresh board in case no board can be loaded
            var boards: Pair<Board, Board>? = null
            if (!regen && sharedPreferences.contains(appContext.getString(R.string.pref_board))) {
                try {

                    val board = gson.fromJson(sharedPreferences.getString(
                            appContext.getString(R.string.pref_board), ""),
                            IntBoard::class.java)

                    val startingBoard = board.copy()
                    for (i in 0 until SudokuSolver.Board.BOARD_SIZE) {
                        startingBoard.apply {
                            if (!isStartingValue(i)) {
                                setValue(i, 1, false)
                                removePossibleValue(i, 1)
                            }
                        }
                    }
                    val solutions = SudokuSolver(SudokuSolver.defaultSolvers).solve(startingBoard)
                    if (solutions.isNotEmpty()) {
                        boards = Pair<Board, Board>(board, solutions[0])
                    }
                } catch (ignored: JSONException) {
                }
            }

            if (boards == null) {
                boards = SudokuSolver.generate()
            }
            liveData.postValue(boards)
        }
        return liveData
    }

    fun saveBoard(board: Board) {
        executor.execute {
            //serializes the board to JSON and saves it to sharedpreferences
            sharedPreferences.edit {
                putString(
                        appContext.getString(R.string.pref_board),
                        gson.toJson(board)
                )
            }
        }
    }

}
