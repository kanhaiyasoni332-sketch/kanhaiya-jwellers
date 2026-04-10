package com.kanhaiyajewellers.creditmanager.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class MinimalLineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val creditPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF4D4D") // status_pending red
        strokeWidth = 6f
        style = Paint.Style.STROKE
    }

    private val receivedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4CAF50") // status_completed green
        strokeWidth = 6f
        style = Paint.Style.STROKE
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#33BFBFBF") // 20% text_secondary
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val creditPath = Path()
    private val receivedPath = Path()

    // Data points
    private var creditData: List<Float> = listOf(10f, 30f, 25f, 45f, 40f, 60f, 55f)
    private var receivedData: List<Float> = listOf(5f, 20f, 15f, 35f, 30f, 50f, 45f)

    fun setData(credit: List<Float>, received: List<Float>) {
        this.creditData = credit
        this.receivedData = received
        invalidate() // Request redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        // Draw subtle grid (4 horizontal lines)
        for (i in 1..3) {
            val y = h * i / 4
            canvas.drawLine(0f, y, w, y, gridPaint)
        }
        
        // Draw 6 vertical lines
        for (i in 1..5) {
            val x = w * i / 6
            canvas.drawLine(x, 0f, x, h, gridPaint)
        }

        if (creditData.isEmpty()) return

        val maxVal = maxOf(creditData.maxOrNull() ?: 0f, receivedData.maxOrNull() ?: 0f) * 1.2f
        val stepX = w / (creditData.size - 1)

        // Credit Path
        creditPath.reset()
        for (i in creditData.indices) {
            val x = i * stepX
            val y = h - (creditData[i] / maxVal * h)
            if (i == 0) creditPath.moveTo(x, y)
            else {
                val prevX = (i - 1) * stepX
                val prevY = h - (creditData[i - 1] / maxVal * h)
                // Smooth bezier 
                val cx1 = prevX + (x - prevX) / 2
                val cy1 = prevY
                val cx2 = prevX + (x - prevX) / 2
                val cy2 = y
                creditPath.cubicTo(cx1, cy1, cx2, cy2, x, y)
            }
        }
        canvas.drawPath(creditPath, creditPaint)

        // Received Path
        receivedPath.reset()
        for (i in receivedData.indices) {
            val x = i * stepX
            val y = h - (receivedData[i] / maxVal * h)
            if (i == 0) receivedPath.moveTo(x, y)
            else {
                val prevX = (i - 1) * stepX
                val prevY = h - (receivedData[i - 1] / maxVal * h)
                val cx1 = prevX + (x - prevX) / 2
                val cy1 = prevY
                val cx2 = prevX + (x - prevX) / 2
                val cy2 = y
                receivedPath.cubicTo(cx1, cy1, cx2, cy2, x, y)
            }
        }
        canvas.drawPath(receivedPath, receivedPaint)
    }
}
