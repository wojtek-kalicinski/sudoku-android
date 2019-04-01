package me.kalicinski.sudoku

import kotlinx.cinterop.convert
import kotlinx.cinterop.staticCFunction
import platform.darwin.dispatch_async_f
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue
import platform.posix.QOS_CLASS_USER_INITIATED
import kotlin.native.concurrent.*

fun <T> runAsyncThenMain(asyncJob: () -> T, mainJob: (T) -> Unit) {
    asyncJob.freeze()
    mainJob.freeze()
    dispatch_async_f(
            dispatch_get_global_queue(QOS_CLASS_USER_INITIATED.convert(), 0),
            DetachedObjectGraph { Pair(asyncJob, mainJob) }.asCPointer(),
            staticCFunction { ptr ->
                initRuntimeIfNeeded()
                val jobs = DetachedObjectGraph<Pair<() -> T, (T) -> Unit>>(ptr).attach()
                val t = DetachedObjectGraph { Pair(jobs.first(), jobs.second) }.asCPointer()
                dispatch_async_f(
                        dispatch_get_main_queue(),
                        t,
                        staticCFunction { ptr2 ->
                            val pair = DetachedObjectGraph<Pair<T, (T) -> Unit>>(ptr2).attach()
                            pair.second(pair.first)
                        }
                )
            });
}