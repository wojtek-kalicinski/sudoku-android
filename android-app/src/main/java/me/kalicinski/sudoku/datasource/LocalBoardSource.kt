package me.kalicinski.sudoku.datasource

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.content.edit
import com.google.gson.*
import me.kalicinski.sudoku.engine.IntBoard
import me.kalicinski.sudoku.engine.SudokuBoard
import me.kalicinski.sudoku.engine.SudokuGame
import me.kalicinski.sudoku.engine.SudokuSolver
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

    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    var game: SudokuGame?
        get() {
            return sharedPreferences.getString(PREF_BOARD, null)?.let {
                println(it)
                val loadedBoard = gson.fromJson(it, SudokuGame::class.java)
                loadedBoard.calculateSolution()
                return loadedBoard
            }
        }
        set(value) {
            sharedPreferences.edit {
                if (value != null) {
                    putString(
                            PREF_BOARD,
                            gson.toJson(value)
                    )
                } else {
                    remove(PREF_BOARD)
                }
            }
        }
}