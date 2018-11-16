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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.kalicinski.sudoku.datasource.GenerateBoardSource
import me.kalicinski.sudoku.datasource.LocalBoardSource
import me.kalicinski.sudoku.engine.SudokuBoard
import me.kalicinski.sudoku.engine.SudokuGame
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardRepository @Inject constructor(
        val localSource: LocalBoardSource,
        val generator: GenerateBoardSource
) {
    private val executor = Executors.newSingleThreadExecutor()

    fun getBoard(regen: Boolean, seed: Long): LiveData<SudokuGame> {
        val liveData = MutableLiveData<SudokuGame>()
        executor.execute {
            var game: SudokuGame? = null
            if (!regen) {
                game = localSource.game
            }

            if (game == null) {
                game = generator.generateBoard(seed)
            }
            liveData.postValue(game)
        }
        return liveData
    }

    fun saveBoard(game: SudokuGame?) {
        executor.execute {
            localSource.game = game
        }
    }

}
