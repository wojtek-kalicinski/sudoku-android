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
package me.kalicinski.sudoku.datasource

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import me.kalicinski.multiplatform.MultiStorage
import me.kalicinski.sudoku.engine.SudokuGame

private const val PREF_BOARD = "PREF_BOARD"

class LocalBoardSource constructor(private val storage: MultiStorage) {

    private val serializer = SudokuGame.serializer()

    var game: SudokuGame?
        get() {
            return storage.getString(PREF_BOARD)?.let {
                println("read from storage: $it")
                try {
                    val loadedBoard = Json.decodeFromString(serializer, it)
                    loadedBoard.calculateSolution()
                    return loadedBoard
                } catch (e: SerializationException) {
                    // format changed or corrupt data, just return null
                    return null
                }
            }
        }
        set(value) {
            storage.run {
                if (value != null) {
                    println("saving $value")
                    putString(
                            PREF_BOARD,
                            Json.encodeToString(serializer, value)
                    )
                } else {
                    putString(PREF_BOARD, null)
                }
            }
        }
}