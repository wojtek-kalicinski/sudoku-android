package me.kalicinski.sudoku.engine

import kotlin.random.Random

fun <T> MutableList<T>.shuffle(random: Random): MutableList<T> {
    if (this is RandomAccess){
        var tmpObject: T
        var rndIndex: Int
        for (i in size downTo 1){
            rndIndex = random.nextInt(i)
            tmpObject = this[rndIndex]
            this[rndIndex] = this[i-1]
            this[i-1] = tmpObject
        }
    }
    return this
}