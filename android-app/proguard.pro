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

-keep class android.support.v7.app.AppCompatViewInflater

-dontwarn retrofit2.Call