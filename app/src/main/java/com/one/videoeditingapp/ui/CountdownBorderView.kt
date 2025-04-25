package com.one.videoeditingapp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.View

class CountdownBorderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val cornerRadius : Float = 12f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 15f
        strokeCap = Paint.Cap.SQUARE
    }

    private val path = Path()
    private var progress = 1f
    private lateinit var pathMeasure: PathMeasure
    private val animatedPath = Path()

    fun setProgress(value: Float) {
        progress = value
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val pad = paint.strokeWidth / 2
        val left = pad
        val top = pad
        val right = w - pad
        val bottom = h - pad

        path.reset()

//        // 👇 START FROM TOP-RIGHT corner and move clockwise
//        path.moveTo(right, top)
//        path.lineTo(left, top) // Right
//        path.lineTo(left, bottom)  // Bottom
//        path.lineTo(right, bottom)     // Left
//        path.lineTo(right, top)    // Top

        path.addRoundRect(
            left, top, right, bottom,
            cornerRadius, cornerRadius,
            Path.Direction.CCW
        )

        pathMeasure = PathMeasure(path, false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!::pathMeasure.isInitialized) return

        val totalLength = pathMeasure.length
        val segmentLength = totalLength * progress

        animatedPath.reset()
        pathMeasure.getSegment(0f, segmentLength, animatedPath, true)

        canvas.drawPath(animatedPath, paint)
    }
}


