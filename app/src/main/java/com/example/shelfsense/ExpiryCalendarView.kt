package com.example.shelfsense

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExpiryCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val calendar = Calendar.getInstance()

    private var items: List<Item> = emptyList()

    private val datePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val weekPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Callback when user taps a date
    var onDateClick: ((String) -> Unit)? = null

    init {
        datePaint.color = 0xFF292333.toInt()
        datePaint.textSize = 14f
        datePaint.textAlign = Paint.Align.CENTER

        weekPaint.color = 0xFF776F82.toInt()
        weekPaint.textSize = 11f
        weekPaint.textAlign = Paint.Align.CENTER

        dotPaint.style = Paint.Style.FILL
    }

    fun setItems(newItems: List<Item>) {
        items = newItems
        invalidate()
    }

    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        invalidate()
    }

    fun previousMonth() {
        calendar.add(Calendar.MONTH, -1)
        invalidate()
    }

    fun getMonthYear(): String {
        val format =
            SimpleDateFormat(
                "MMMM yyyy",
                Locale.getDefault()
            )

        return format.format(calendar.time)
    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        val columnWidth = width / 7f

        val weekHeight = 30f

        val dateAreaHeight =
            (height - weekHeight) / 6f

        val weekNames =
            arrayOf(
                "S",
                "M",
                "T",
                "W",
                "T",
                "F",
                "S"
            )

        // Week names

        for (i in weekNames.indices) {

            val x =
                columnWidth * i +
                        columnWidth / 2

            canvas.drawText(
                weekNames[i],
                x,
                18f,
                weekPaint
            )
        }

        // First day of month

        val firstDay =
            calendar.clone() as Calendar

        firstDay.set(
            Calendar.DAY_OF_MONTH,
            1
        )

        val firstDayOfWeek =
            firstDay.get(Calendar.DAY_OF_WEEK) - 1

        val daysInMonth =
            calendar.getActualMaximum(
                Calendar.DAY_OF_MONTH
            )

        // Draw dates

        for (day in 1..daysInMonth) {

            val position =
                firstDayOfWeek + day - 1

            val row =
                position / 7

            val column =
                position % 7

            val x =
                columnWidth * column +
                        columnWidth / 2

            val y =
                weekHeight +
                        dateAreaHeight * row +
                        dateAreaHeight / 2 +
                        5

            datePaint.typeface =
                Typeface.DEFAULT

            // Highlight today

            val today =
                Calendar.getInstance()

            if (
                day == today.get(Calendar.DAY_OF_MONTH) &&
                calendar.get(Calendar.MONTH) ==
                today.get(Calendar.MONTH) &&
                calendar.get(Calendar.YEAR) ==
                today.get(Calendar.YEAR)
            ) {

                datePaint.typeface =
                    Typeface.DEFAULT_BOLD
            }

            canvas.drawText(
                day.toString(),
                x,
                y,
                datePaint
            )

            drawExpiryDot(
                canvas,
                day,
                x,
                y + 10
            )
        }
    }

    private fun drawExpiryDot(
        canvas: Canvas,
        day: Int,
        x: Float,
        y: Float
    ) {

        val dateCalendar =
            calendar.clone() as Calendar

        dateCalendar.set(
            Calendar.DAY_OF_MONTH,
            day
        )

        val dateFormat =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            )

        val dateString =
            dateFormat.format(
                dateCalendar.time
            )

        val matchingItem =
            items.firstOrNull {
                it.expiryDate == dateString
            }

        if (matchingItem == null) {
            return
        }

        val days =
            ExpiryUtils.getDaysUntilExpiry(
                matchingItem.expiryDate
            )

        dotPaint.color =
            when {

                days < 0 ->
                    0xFFE53935.toInt()

                days <= 7 ->
                    0xFFFF9800.toInt()

                else ->
                    0xFF4CAF50.toInt()
            }

        canvas.drawCircle(
            x,
            y,
            4f,
            dotPaint
        )
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (
            event.action !=
            MotionEvent.ACTION_UP
        ) {
            return true
        }

        val columnWidth =
            width.toFloat() / 7f

        val weekHeight = 30f

        val dateAreaHeight =
            (height.toFloat() - weekHeight) / 6f

        // Which column was tapped?

        val column =
            (event.x / columnWidth)
                .toInt()
                .coerceIn(0, 6)

        // Which row was tapped?

        val row =
            ((event.y - weekHeight) /
                    dateAreaHeight)
                .toInt()
                .coerceIn(0, 5)

        // First day of current month

        val firstDay =
            calendar.clone() as Calendar

        firstDay.set(
            Calendar.DAY_OF_MONTH,
            1
        )

        val firstDayOfWeek =
            firstDay.get(
                Calendar.DAY_OF_WEEK
            ) - 1

        // Calculate selected day

        val day =
            row * 7 +
                    column -
                    firstDayOfWeek +
                    1

        val daysInMonth =
            calendar.getActualMaximum(
                Calendar.DAY_OF_MONTH
            )

        if (
            day < 1 ||
            day > daysInMonth
        ) {
            return true
        }

        // Create selected date

        val selectedCalendar =
            calendar.clone() as Calendar

        selectedCalendar.set(
            Calendar.DAY_OF_MONTH,
            day
        )

        val dateFormat =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            )

        val selectedDate =
            dateFormat.format(
                selectedCalendar.time
            )

        // Send date to MainActivity

        onDateClick?.invoke(
            selectedDate
        )

        return true
    }
}