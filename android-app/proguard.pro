-repackageclasses

-dontwarn android.databinding.DataBinderMapper
-dontwarn android.databinding.DataBindingUtil
-dontwarn android.databinding.adapters.CardViewBindingAdapter

-dontwarn com.google.errorprone.annotations.**

-keepnames class me.kalicinski.sudoku.engine.IntBoard {
    <fields>;
}
-keepnames class me.kalicinski.sudoku.engine.SudokuGame {
    <fields>;
}

