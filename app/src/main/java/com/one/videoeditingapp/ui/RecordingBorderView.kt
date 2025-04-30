package com.one.videoeditingapp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.View

class RecordingBorderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val cornerRadius: Float = 100f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
        strokeCap = Paint.Cap.ROUND
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

        canvas.save()
        canvas.rotate(90f, width / 2f, height / 2f)

        canvas.drawPath(animatedPath, paint)

        canvas.restore()
    }
}


