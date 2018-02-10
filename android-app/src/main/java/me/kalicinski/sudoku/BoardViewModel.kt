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
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.os.Handler
import android.os.Message
import me.kalicinski.sudoku.engine.SudokuBoard
import javax.inject.Inject


class BoardViewModel @Inject constructor(val repository: BoardRepository) : ViewModel() {
    private var solvedBoard: SudokuBoard? = null
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
                repository.saveBoard(msg.obj as SudokuBoard)
            } else {
                super.handleMessage(msg)
            }
        }
    }

    init {
        board.observeForever {
            if (it != null) {
                handler.removeMessages(MSG_SAVE)
                val msg = handler.obtainMessage(MSG_SAVE, it)
                handler.sendMessageDelayed(msg, (1000 * 10).toLong())
            }
        }
    }

    fun saveNow() {
        board.value?.let {
            handler.removeMessages(MSG_SAVE)
            repository.saveBoard(it)
        }
    }

    fun initIfEmpty() {
        if (board.value == null) {
            generateNewBoard(false)
        }
    }

    fun generateNewBoard(regen: Boolean) {
        busy.value = true
        val newBoards = repository.getBoard(regen)
        newBoards.observeForever(object : Observer<Pair<SudokuBoard, SudokuBoard>> {
            override fun onChanged(b: Pair<SudokuBoard, SudokuBoard>?) {
                b?.let {
                    board.value = it.first
                    solvedBoard = it.second
                }
                mistakes.value = null
                busy.value = false
                newBoards.removeObserver(this)
            }
        })
    }

    fun solveBoard() {
        board.value = solvedBoard?.copy()
    }

    fun checkBoard() {
        busy.value = true
        board.value?.let { currentBoard ->
            if (solvedBoard != null) {
                val errors = BooleanArray(SudokuBoard.BOARD_SIZE) {
                    currentBoard.isCommitedValue(it)
                            && currentBoard.getFirstPossibleValue(it) != solvedBoard?.getFirstPossibleValue(it)
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
