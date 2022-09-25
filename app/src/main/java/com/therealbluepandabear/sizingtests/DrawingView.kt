package com.therealbluepandabear.sizingtests

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var drawingViewBitmap: Bitmap
    private lateinit var boundingRect: Rect

    private var scaleWidth = 0f
    private var scaleHeight = 0f

    private var bitmapWidth = 10
    private var bitmapHeight = 10

    private val rectPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        setShadowLayer(10f,0f, 0f, Color.argb(100, 0, 0, 0))
    }

    private fun setScaleWH() {
        scaleWidth = measuredWidth.toFloat() / drawingViewBitmap.width.toFloat()
        scaleHeight = measuredHeight.toFloat() / drawingViewBitmap.height.toFloat()
    }

    private fun setBoundingRect() {
        val centerOfCanvas = Point(width / 2, height / 2)
        val rectW = 1000
        val rectH = 1000
        val left = centerOfCanvas.x - rectW / 2
        val top = centerOfCanvas.y - rectH / 2
        val right = centerOfCanvas.x + rectW / 2
        val bottom = centerOfCanvas.y + rectH / 2

        boundingRect = Rect(left, top, right, bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (::drawingViewBitmap.isInitialized) {
            drawingViewBitmap.recycle()
        }

        drawingViewBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)

        setBoundingRect()
        setScaleWH()
        requestLayout()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val coordinateX = ((event.x) / scaleWidth).toInt()
        val coordinateY = ((event.y) / scaleHeight).toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (coordinateX in 0 until drawingViewBitmap.width && coordinateY in 0 until drawingViewBitmap.height) {
                    drawingViewBitmap.setPixel(coordinateX, coordinateY, Color.BLACK)
                }
            }

            MotionEvent.ACTION_UP -> {
                drawingViewBitmap.drawTransparent()
            }
        }

        invalidate()

        return true
    }

    override fun onDraw(canvas: Canvas) {
        if (::drawingViewBitmap.isInitialized) {
            canvas.drawRect(boundingRect, rectPaint)
            canvas.drawBitmap(drawingViewBitmap, null, boundingRect, null)
        }
    }
}