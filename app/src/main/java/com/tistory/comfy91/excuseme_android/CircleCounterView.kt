package com.tistory.comfy91.excuseme_android


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes


class CircleCounterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var startAngle: Float = -90f
    var angle: Float = 120f
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var text: String = "13"
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var circleWidth = 10f
        set(value) {
            field = value
            setCirclePaint()
            invalidate()
            requestLayout()
        }
    var textSize = 26f
        set(value) {
            field = value
            setCounterPaint()
            invalidate()
            requestLayout()
        }
    @ColorRes
    var circleColor: Int = R.color.mainpink
        set(value) {
            field = value
            setCirclePaint()
            invalidate()
            requestLayout()
        }
    @ColorRes
    var textColor: Int = R.color.black
        set(value) {
            field = value
            setCounterPaint()
            invalidate()
            requestLayout()
        }


    private var circlePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = circleColor
        strokeWidth = circleWidth
        style = Paint.Style.STROKE
    }
    private var counterPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = textSize
        textAlign = Paint.Align.CENTER
    }

    private fun setCirclePaint() {
        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = circleColor
            strokeWidth = circleWidth
            style = Paint.Style.STROKE
        }
    }

    private fun setCounterPaint() {
        counterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            textSize = textSize
            textAlign = Paint.Align.CENTER
        }
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CircleCounterView,
            defStyleAttr, 0
        ).apply {
            try {
                startAngle = getFloat(R.styleable.CircleCounterView_startAngle, -90f)
                circleWidth = getDimension(
                    R.styleable.CircleCounterView_circleWidth,
                    26f
                )
                @Suppress("DEPRECATION")
                circleColor = getColor(
                    R.styleable.CircleCounterView_circleColor,
                    resources.getColor(R.color.mainpink)
                )
                textSize = getDimension(
                    R.styleable.CircleCounterView_textSize,
                    10f
                )
                @Suppress("DEPRECATION")
                textColor = getColor(
                    R.styleable.CircleCounterView_textColor,
                    resources.getColor(R.color.black)
                )
                text = getString(
                    R.styleable.CircleCounterView_text
                ).orEmpty()
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            drawArc(
                paddingLeft + circleWidth,
                paddingTop + circleWidth,
                width.toFloat() - paddingRight - circleWidth,
                height.toFloat() - paddingBottom - circleWidth,
                startAngle,
                angle,
                false,
                circlePaint
            )
            drawText(text, width / 2f, height / 2f, counterPaint)
        }
    }
}