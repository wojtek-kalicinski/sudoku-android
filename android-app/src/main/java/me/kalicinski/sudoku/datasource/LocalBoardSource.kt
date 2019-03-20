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

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify
import me.kalicinski.multiplatform.MultiStorage
import me.kalicinski.sudoku.engine.IntBoard
import me.kalicinski.sudoku.engine.SudokuBoard
import me.kalicinski.sudoku.engine.SudokuGame
import java.lang.reflect.Type
import javax.inject.Inject

private const val PREF_BOARD = "PREF_BOARD"

class LocalBoardSource @Inject constructor(context: Context) {

    private val appContext: Context = context.applicationContext
    private val gson = GsonBuilder().apply {
        registerTypeAdapter(SudokuBoard::class.java, object : JsonDeserializer<SudokuBoard> {
            override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): SudokuBoard {
                return context?.deserialize(json, IntBoard::class.java)!!
            }
        })
        registerTypeAdapter(SudokuBoard::class.java, object : JsonSerializer<SudokuBoard> {
            override fun serialize(src: SudokuBoard?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                return context?.serialize(src, IntBoard::class.java)!!
            }
        })

    }.create()

    private val storage: MultiStorage by lazy {
        MultiStorage(PreferenceManager.getDefaultSharedPreferences(appContext))
    }

    var game: SudokuGame?
        get() {
            return storage.getString(PREF_BOARD)?.let {
                println("read from storage: $it")
                val loadedBoard = gson.fromJson(it, SudokuGame::class.java)
                loadedBoard.calculateSolution()
                return loadedBoard
            }
        }
        set(value) {
            storage.run {
                if (value != null) {
                    putString(
                            PREF_BOARD,
                            gson.toJson(value)
                    )
                    println("kotlinx.serialized:" + Json.stringify(SudokuGame.serializer(), value))
                } else {
                    putString(PREF_BOARD, null)
                }
            }
        }
}