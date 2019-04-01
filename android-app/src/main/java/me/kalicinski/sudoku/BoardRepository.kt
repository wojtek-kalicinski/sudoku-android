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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.kalicinski.sudoku.datasource.GenerateBoardSource
import me.kalicinski.sudoku.datasource.LocalBoardSource
import me.kalicinski.sudoku.engine.SudokuGame
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardRepository @Inject constructor(
        val localSource: LocalBoardSource,
        val generator: GenerateBoardSource
) {
    suspend fun getBoard(regen: Boolean, seed: Long): SudokuGame = withContext(Dispatchers.Default) {
        var game: SudokuGame? = null
        if (!regen) {
            game = localSource.game
        }

        if (game == null) {
            game = generator.generateBoard(seed)
        }
        game as SudokuGame
    }

    suspend fun saveBoard(game: SudokuGame?) = withContext(Dispatchers.Default) {
        localSource.game = game
    }
}
