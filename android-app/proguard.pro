-repackageclasses

-keepnames class me.kalicinski.sudoku.engine.IntBoard {
    <fields>;
}
-keepnames class me.kalicinski.sudoku.engine.SudokuGame {
    <fields>;
}

-assumenosideeffects class kotlinx.coroutines.internal.MainDispatcherLoader {
    boolean FAST_SERVICE_LOADER_ENABLED return false;
}
# the above rule doesn't seem to be working?
#-checkdiscard class kotlinx.coroutines.internal.FastServiceLoader
