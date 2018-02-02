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

package me.kalicinski.sudoku

import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import me.kalicinski.sudoku.engine.SudokuSolver
import me.kalicinski.sudoku.engine.SudokuSolver.Board

class BoardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {
    private val cells = Array(SudokuSolver.Board.BOARD_SIZE) {
        CellView(context).apply {
            setTag(R.id.tag_cell_index, it)
        }
    }
    private val paint = Paint()
    private val paint2 = Paint()
    private val strokeWidthNormal: Float
    private val strokeWidthBold: Float

    var onBoardChangedListener: OnBoardChangeListener? = null

    private fun fireBoardChangedEvent(cellView: CellView) {
        onBoardChangedListener?.onBoardChanged(
                cellView.getTag(R.id.tag_cell_index) as Int,
                cellView.isNumberConfirmed,
                cellView.getNumbersShowing()
        )
    }

    init {
        setWillNotDraw(false)
        cells.forEach {
            addView(it)
        }
        paint.apply {
            color = ContextCompat.getColor(context, R.color.paint_color_1)
            style = Paint.Style.STROKE
        }
        paint2.apply {
            color = ContextCompat.getColor(context, R.color.paint_color_2)
            style = Paint.Style.STROKE
        }
        strokeWidthBold = context.resources.getDimension(R.dimen.line_width_bold)
        strokeWidthNormal = context.resources.getDimension(R.dimen.line_width)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        val spec = if (width > height) heightMeasureSpec else widthMeasureSpec
        super.onMeasure(spec, spec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (changed) {
            val w = right - left
            val h = bottom - top
            val size = Math.min(w, h)
            val cellSize = size / 9f

            cells.forEachIndexed { i, cellView ->
                val x = Board.getColumn(i)
                val y = Board.getRow(i)
                cellView.layout(
                        (x * cellSize).toInt(),
                        (y * cellSize).toInt(),
                        ((x + 1) * cellSize).toInt(),
                        ((y + 1) * cellSize).toInt())
                val centerX = x * cellSize + cellSize / 2
                val centerY = y * cellSize + cellSize / 2
                val scale = context.resources.getInteger(R.integer.zoom_scale)
                var pivotX = cellSize / 2
                var pivotY = cellSize / 2

                if (centerX - cellSize / 2 * 4 < 0) {
                    pivotX = cellSize / 2 + (centerX / 3f - scale / 2f * cellSize / 3f)
                } else if (centerX + cellSize / 2 * 4 > w) {
                    pivotX = cellSize / 2 + (centerX - w + scale / 2f * cellSize) / 3f
                }
                if (centerY - cellSize / 2 * 4 < 0) {
                    pivotY = cellSize / 2 + (centerY / 3f - scale / 2f * cellSize / 3f)
                } else if (centerY + cellSize / 2 * 4 > h) {
                    pivotY = cellSize / 2 + (centerY - h + scale / 2f * cellSize) / 3f
                }
                cellView.pivotX = pivotX
                cellView.pivotY = pivotY
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0..9) {
            if (i % 3 == 0) {
                paint.strokeWidth = strokeWidthBold
                if (i > 0 && i < 9) {
                    canvas.drawLine(i * width / 9f, 0f, i * width / 9f, height.toFloat(), paint)
                }
                canvas.drawLine(0f, i * height / 9f, width.toFloat(), i * height / 9f, paint)
            } else {
                paint.strokeWidth = strokeWidthNormal
                if (i > 0 && i < 9) {
                    canvas.drawLine(i * width / 9f, 0f, i * width / 9f, height.toFloat(), paint2)
                }
                canvas.drawLine(0f, i * height / 9f, width.toFloat(), i * height / 9f, paint2)
            }
        }
    }

    fun setBoard(board: Board?) {
        cells.forEachIndexed { i, cellView ->
            cellView.apply {
                setNumbersShown(board?.possibleValues(i) ?: emptySet())
                isNumberConfirmed = board?.isCommitedValue(i) == true
                isChangeable = board?.isStartingValue(i) == false
            }
        }
    }

    fun setMistakes(mistakes: BooleanArray?) {
        cells.forEachIndexed { i, cellView ->
            cellView.isNumberIncorrect = mistakes?.get(i) == true
        }
    }

    interface OnBoardChangeListener {
        fun onBoardChanged(cellIndex: Int, isConfirmed: Boolean, numbersShown: Set<Int>)
    }

    inner class CellView @JvmOverloads constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0,
            defStyleRes: Int = 0
    ) : View(context, attrs, defStyleAttr, defStyleRes) {
        private val paint = Paint()
        private val textBounds = Rect()
        private val numbersShowing = HashSet<Int>(9)
        var isNumberConfirmed = false
            set(numberConfirmed) {
                if (numberConfirmed && numbersShowing.size > 1) {
                    throw IllegalStateException("More than 1 number showing")
                }
                field = numberConfirmed
                invalidate()
            }
        var isNumberIncorrect = false
            set(numberIncorrect) {
                field = numberIncorrect
                invalidate()
            }
        private val gestureDetector: GestureDetector

        private val gestureDetectorListener = object : GestureListenerEx() {
            private var isLongTouchState = false

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                if (!isFocused) {
                    requestFocus()
                } else if (!isSelected) {
                    bringToFront()
                    isSelected = true
                } else {
                    val x = e.x / width
                    val y = e.y / height
                    val col = Math.floor((x / (1 / 3f)).toDouble()).toInt()
                    val row = Math.floor((y / (1 / 3f)).toDouble()).toInt()
                    val number = row * 3 + col + 1
                    setNumberShown(number, !getNumbersShowing().contains(number))
                    fireBoardChangedEvent(this@CellView)
                }
                return true
            }

            override fun onScroll(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
            ): Boolean {
                if (!isLongTouchState) {
                    requestFocus()
                    bringToFront()
                    isSelected = true
                }
                isLongTouchState = true
                return super.onScroll(e1, e2, distanceX, distanceY)
            }

            override fun onLongPress(e: MotionEvent) {
                isLongTouchState = true
                requestFocus()
                bringToFront()
                isSelected = true
            }

            override fun onDown(e: MotionEvent): Boolean {
                isLongTouchState = false
                return true
            }

            override fun onUp(ev: MotionEvent) {
                if (isLongTouchState) {
                    val x = ev.x / width
                    val y = ev.y / height
                    val col = Math.floor((x / (1 / 3f)).toDouble()).toInt()
                    val row = Math.floor((y / (1 / 3f)).toDouble()).toInt()
                    val number = row * 3 + col + 1
                    if (x >= 0 && x <= 1 && y >= 0 && y <= 1) {
                        setNumbersShown(setOf(number))
                        isNumberConfirmed = true
                        fireBoardChangedEvent(this@CellView)
                    }
                    isSelected = false
                }
            }

            override fun onMove(ev: MotionEvent) {
                if (isLongTouchState) {
                    val x = ev.x / width
                    val y = ev.y / height
                    val col = Math.floor((x / (1 / 3f)).toDouble()).toInt()
                    val row = Math.floor((y / (1 / 3f)).toDouble()).toInt()
                    val number = row * 3 + col + 1
                    if (x >= 0 && x <= 1 && y >= 0 && y <= 1) {
                        setNumbersShown(setOf(number))
                        isNumberConfirmed = true
                    } else {
                        setNumbersShown(emptySet())
                        isNumberConfirmed = false
                    }
                }
            }
        }

        private val highlightColor: Int
        private var bigNumberHeight: Float = 0F
        private var smallNumberHeight: Float = 0F
        var isChangeable: Boolean = false
            set(value) {
                field = value
                isEnabled = isChangeable
                setOnClickListener(if (isChangeable) clickListener else null)
                invalidate()
            }


        private val clickListener = { _: View ->
            if (!isSelected) {
                requestFocus()
                bringToFront()
                isSelected = true
            }
        }

        private open inner class GestureListenerEx : GestureDetector.SimpleOnGestureListener() {
            internal open fun onUp(ev: MotionEvent) {}
            internal open fun onMove(ev: MotionEvent) {}
        }

        override fun onFocusChanged(
                gainFocus: Boolean,
                direction: Int,
                previouslyFocusedRect: Rect?
        ) {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
            if (!gainFocus) {
                isSelected = false
            }
        }

        init {
            stateListAnimator = AnimatorInflater.loadStateListAnimator(
                    getContext(),
                    R.animator.cellview_animator
            )

            gestureDetector = object : GestureDetector(getContext(), gestureDetectorListener) {
                override fun onTouchEvent(ev: MotionEvent): Boolean {
                    val returnValue = super.onTouchEvent(ev)
                    when (ev.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_UP -> gestureDetectorListener.onUp(ev)
                        MotionEvent.ACTION_MOVE -> gestureDetectorListener.onMove(ev)
                    }
                    return returnValue
                }
            }
            gestureDetector.setOnDoubleTapListener(null)
            paint.isAntiAlias = true
            paint.textAlign = Paint.Align.CENTER
            isChangeable = true
            isFocusable = true
            isFocusableInTouchMode = true
            highlightColor = ResourcesCompat.getColor(
                    resources,
                    R.color.primary_light,
                    context.theme
            )
        }


        override fun onTouchEvent(event: MotionEvent): Boolean {
            return if (isChangeable) {
                gestureDetector.onTouchEvent(event)
            } else {
                super.onTouchEvent(event)
            }
        }

        fun setNumbersShown(numbers: Collection<Int>?) {
            numbersShowing.clear()
            numbers?.let {
                numbersShowing.addAll(it)
            }
            isNumberConfirmed = false
            isNumberIncorrect = false
            invalidate()
        }

        fun setNumberShown(number: Int, show: Boolean) {
            if (when {
                        show -> numbersShowing.add(number)
                        else -> numbersShowing.remove(number)
                    }) {
                isNumberConfirmed = false
                invalidate()
            }
            isNumberIncorrect = false
        }

        fun getNumbersShowing(): Set<Int> {
            return HashSet(numbersShowing)
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            bigNumberHeight = 0.8f * h
            smallNumberHeight = 0.2f * h
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            if (isFocused) {
                paint.color = ContextCompat.getColor(context, R.color.paint_color_focused)
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }

            paint.color = if (isChangeable) {
                ContextCompat.getColor(context, R.color.paint_color_changeable)
            } else {
                Color.BLACK
            }

            if (isNumberIncorrect) {
                paint.color = ContextCompat.getColor(context, R.color.paint_color_incorrect)
            }

            if (isNumberConfirmed) {
                paint.textSize = bigNumberHeight
                val bigNumber = numbersShowing.iterator().next().toString()
                paint.getTextBounds(bigNumber, 0, 1, textBounds)
                canvas.drawText(
                        bigNumber,
                        0,
                        1,
                        width / 2f,
                        height / 2f - textBounds.bottom
                                + Math.abs(textBounds.bottom - textBounds.top) / 2.0f,
                        paint
                )
            } else {
                for (i in numbersShowing) {
                    val x = (i - 1) % 3
                    val y = (i - 1) / 3
                    val numberHighlighted = 0
                    if (numberHighlighted == i) {
                        val color = paint.color
                        paint.color = highlightColor
                        canvas.drawRect(
                                x * width / 3f,
                                y * height / 3f,
                                (x + 1) * width / 3f,
                                (y + 1) * height / 3f,
                                paint
                        )
                        paint.color = color
                    }

                    paint.textSize = smallNumberHeight
                    paint.getTextBounds(i.toString(), 0, 1, textBounds)
                    val textSizeHalf = Math.abs(textBounds.bottom - textBounds.top) / 2.0f
                    canvas.drawText(
                            i.toString(),
                            0,
                            1,
                            x * width / 3f + width / 6f,
                            y * height / 3f + textBounds.bottom.toFloat()
                                    + textSizeHalf + height / 6f,
                            paint
                    )
                }
            }
        }
    }
}
