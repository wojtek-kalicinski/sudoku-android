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

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import android.os.Handler
import android.os.Message
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.kalicinski.sudoku.engine.IntBoard
import me.kalicinski.sudoku.engine.SudokuBoard
import me.kalicinski.sudoku.engine.SudokuGame
import javax.inject.Inject


class BoardViewModel @Inject constructor(val repository: BoardRepository) : ViewModel() {
    var game: SudokuGame? = null
    val board = MutableLiveData<SudokuBoard>()
    val busy = MutableLiveData<Boolean>()
    val mistakes = MutableLiveData<BooleanArray>()

    companion object {
        const val MSG_SAVE = 1
    }

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == MSG_SAVE) {
                viewModelScope.launch {
                    repository.saveBoard(msg.obj as SudokuGame)
                }
            }
        }
    }

    init {
        board.observeForever {
            if (it != null) {
                game?.board = it as IntBoard
                handler.removeMessages(MSG_SAVE)
                val msg = handler.obtainMessage(MSG_SAVE, game)
                handler.sendMessageDelayed(msg, 5_000L)
            }
        }
    }

    fun saveNow() {
        board.value?.let {
            handler.removeMessages(MSG_SAVE)
            game?.board = it as IntBoard
            viewModelScope.launch {
                repository.saveBoard(game)
            }
        }
    }

    fun initIfEmpty() {
        if (board.value == null) {
            generateNewBoard(false)
        }
    }

    @JvmOverloads
    fun generateNewBoard(regen: Boolean, seed: Long = System.currentTimeMillis()) {
        busy.value = true
        viewModelScope.launch {
            val newGame = repository.getBoard(regen, seed)
            game = newGame
            board.value = newGame.board
            mistakes.value = null
            busy.value = false
        }
    }

    fun solveBoard() {
        board.value = game?.solvedBoard?.copy()
    }

    fun checkBoard() {
        busy.value = true
        board.value?.let { currentBoard ->
            if (game != null) {
                val errors = BooleanArray(SudokuBoard.BOARD_SIZE) {
                    currentBoard.isCommitedValue(it)
                            && currentBoard.getFirstPossibleValue(it) != game?.solvedBoard?.getFirstPossibleValue(it)
                }
                mistakes.value = errors
            }
        }
        busy.value = false
    }

    fun setCell(cellIndex: Int, confirmed: Boolean, numbers: Set<Int>) {
        board.value?.let {
            for (j in 1..9) {
                if (numbers.contains(j)) {
                    it.addPossibleValue(cellIndex, j)
                } else {
                    it.removePossibleValue(cellIndex, j)
                }
            }
            it.setCommitedValue(cellIndex, confirmed)
            board.value = it
            mistakes.value = null
        }
    }
}
