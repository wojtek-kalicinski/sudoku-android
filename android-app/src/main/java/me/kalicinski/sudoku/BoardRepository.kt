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
import me.kalicinski.sudoku.datasource.GenerateBoardSource
import me.kalicinski.sudoku.datasource.LocalBoardSource
import me.kalicinski.sudoku.engine.SudokuBoard
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardRepository @Inject constructor(
        val localSource: LocalBoardSource,
        val generator: GenerateBoardSource
) {
    private val executor = Executors.newSingleThreadExecutor()

    fun getBoard(regen: Boolean): LiveData<Pair<SudokuBoard, SudokuBoard>> {
        val liveData = MutableLiveData<Pair<SudokuBoard, SudokuBoard>>()
        executor.execute {
            var boards: Pair<SudokuBoard, SudokuBoard>? = null
            if (!regen) {
                @Suppress("UNCHECKED_CAST")
                boards = localSource.board as Pair<SudokuBoard, SudokuBoard>?
            }

            if (boards == null) {
                @Suppress("UNCHECKED_CAST")
                boards = generator.board as Pair<SudokuBoard, SudokuBoard>?
            }
            liveData.postValue(boards)
        }
        return liveData
    }

    fun saveBoard(board: SudokuBoard) {
        executor.execute {
            localSource.board = Pair(board, null)
        }
    }

}
