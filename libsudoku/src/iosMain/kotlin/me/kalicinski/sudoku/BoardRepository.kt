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

import kotlinx.cinterop.convert
import kotlinx.cinterop.staticCFunction
import me.kalicinski.sudoku.datasource.GenerateBoardSource
import me.kalicinski.sudoku.datasource.LocalBoardSource
import me.kalicinski.sudoku.engine.SudokuGame
import platform.darwin.dispatch_async_f
import platform.darwin.dispatch_get_global_queue
import platform.posix.QOS_CLASS_USER_INITIATED
import kotlin.native.concurrent.DetachedObjectGraph
import kotlin.native.concurrent.attach
import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze

class BoardRepository constructor(
        val localSource: LocalBoardSource,
        val generator: GenerateBoardSource
) {

    data class GenerateArgs(
            val regen: Boolean,
            val seed: Long,
            val localSource: LocalBoardSource,
            val generator: GenerateBoardSource,
            val callback: ((SudokuGame) -> Unit)
    )

    init {
        localSource.freeze()
        generator.freeze()
        this.ensureNeverFrozen()
    }

    fun getBoard(regen: Boolean, seed: Long, callback: ((SudokuGame) -> Unit)): Unit {
        val localSource = this.localSource
        val generator = this.generator
        runAsyncThenMain({
            var game: SudokuGame? = null
            if (!regen) {
                game = localSource.game
            }

            if (game == null) {
                game = generator.generateBoard(seed)
            }
            game.ensureNeverFrozen()
            game.board.ensureNeverFrozen()
            game as SudokuGame
        }, callback)
    }

    fun saveBoard(game: SudokuGame?) {
        val localSource = this.localSource
        val gameCopy = game?.copy(board = game.board.copy())
        runAsyncThenMain({
            localSource.game = gameCopy
        }, {
            println("saved")
        })
    }
}
