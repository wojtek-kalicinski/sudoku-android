package me.kalicinski.sudoku.engine

abstract class SudokuBoard {

    open fun toArray(): IntArray {
        val board = IntArray(BOARD_SIZE)
        for (i in 0 until BOARD_SIZE) {
            if (possibleValuesNum(i) > 1) {
                throw IllegalStateException("Cannot convert this board to int[], too "
                        + "many possible values")
            }
            board[i] = getFirstPossibleValue(i)
        }
        return board
    }

    /**
     * Returns true if the value in this cell is not a draft.
     * @param pos cell index
     * @return true if value is not a draft
     */
    abstract fun isCommitedValue(pos: Int): Boolean

    abstract fun setCommitedValue(pos: Int, commited: Boolean)

    /**
     * Returns true if the value in this cell is part of the sudoku board and can't be changed
     * @param pos cell index
     * @return true if value is a starting value of the sudoku board
     */
    abstract fun isStartingValue(pos: Int): Boolean

    abstract fun setStartingValue(pos: Int, starting: Boolean)

    /**
     * Checks if there are any numbers currently in this cell
     * @param pos cell index
     * @return true if the cell holds at least one value
     */
    abstract fun hasPossibleValue(pos: Int): Boolean

    /**
     * Checks how many numbers are currently in this cell
     * @param pos cell index
     * @return the number of currently held values
     */
    abstract fun possibleValuesNum(pos: Int): Int

    /**
     * Returns a list of current numbers in this cell
     * @param pos cell index
     * @return List containing all numbers in this cell
     */
    abstract fun possibleValues(pos: Int): List<Int>

    /**
     * Sets a single number in a cell, either as draft or not.
     * This call removes any other numbers from the cell.
     *
     * @param pos cell index
     * @param value number 1-9
     * @param commited true if the number is not a draft
     */
    abstract fun setValue(pos: Int, value: Int, commited: Boolean)

    /**
     * Unsets a number in a cell if it was set.
     * @param pos cell index
     * @param value number 1-9
     * @return true if the operation changed the board, false if it was no-op
     */
    abstract fun removePossibleValue(pos: Int, value: Int): Boolean

    /**
     * Sets a number in a cell if it was not set
     * @param pos cell index
     * @param value number 1-9
     * @return true if the operation changed the board, false if it was no-op
     */
    abstract fun addPossibleValue(pos: Int, value: Int): Boolean

    /**
     * Checks if the given number is currently set
     * @param pos cell index
     * @param value number 1-9 to check
     * @return true if the cell currently has the number set
     */
    abstract fun isValuePossible(pos: Int, value: Int): Boolean

    /**
     * Gets the first number that is set in this cell, if any
     * @param pos cell index
     * @return first number that is set, counting from 1-9
     */
    abstract fun getFirstPossibleValue(pos: Int): Int

    /**
     * Clears any values and state on the given cell
     * @param pos cell index
     */
    abstract fun clearValues(pos: Int)

    /**
     * Returns an deep copy of this board.
     * @return copy of this board
     */
    abstract fun copy(): SudokuBoard

    companion object {
        const val BOARD_SIZE = 9 * 9
        val NUMBERS = (1..9).toList()

        @Suppress("NOTHING_TO_INLINE")
        inline fun getIndex(x: Int, y: Int) = y * 9 + x

        /**
         * Get field column from field index
         * @param i board cell index
         * @return 0-based column number for the given cell
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun getColumn(i: Int) = i % 9

        /**
         * Get field row from field index
         * @param i board cell index
         * @return 0-based row number for the given cell
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun getRow(i: Int) = i / 9

        /**
         * Get index of the first cell in the column with the given cell
         * @param i board cell index
         * @return index of first cell in the same column
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun getColumnStart(i: Int) = getIndex(i % 9, 0)

        /**
         * Get index of the first cell in the row with the given cell
         * @param i board cell index
         * @return index of first cell in the same row
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun getRowStart(i: Int) = getIndex(0, i / 9)

        /**
         * Get index of the first cell in the same 3x3 square as the given cell
         * @param i board cell index
         * @return index of first cell in the same square
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun getSquareStart(i: Int): Int {
            // has to be row: 0, 3, 6 and column: 0, 3, 6
            return getIndex(getColumn(i) / 3 * 3, getRow(i) / 3 * 3)
        }
    }

}