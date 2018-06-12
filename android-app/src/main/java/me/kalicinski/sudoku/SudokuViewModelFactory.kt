package me.kalicinski.sudoku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Lazy
import javax.inject.Inject

class SudokuViewModelFactory @Inject constructor(val boardViewModel: Lazy<BoardViewModel>)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BoardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return boardViewModel.get() as T
        }
        throw IllegalArgumentException("unknown model class " + modelClass)
    }
}