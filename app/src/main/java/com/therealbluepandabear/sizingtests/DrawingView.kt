package com.therealbluepandabear.sizingtests

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var drawingViewBitmap: Bitmap
    private lateinit var boundingRect: Rect

    private var scaleWidth = 0f
    private var scaleHeight = 0f

    private var bitmapWidth = 5
    private var bitmapHeight = 6

    private val rectPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        setShadowLayer(10f,0f, 0f, Color.argb(100, 0, 0, 0))
    }

    private fun setScaleWH() {
        scaleWidth = boundingRect.width() / drawingViewBitmap.width.toFloat()
        scaleHeight = boundingRect.height() / drawingViewBitmap.height.toFloat()
    }

    private fun setBoundingRect() {
        val ratio = if (bitmapWidth > bitmapHeight) {
            bitmapHeight.toDouble() / bitmapWidth.toDouble()
        } else {
            bitmapWidth.toDouble() / bitmapHeight.toDouble()
        }

        val rectW: Int = if (bitmapWidth > bitmapHeight) {
            width
        } else if (bitmapHeight > bitmapWidth) {
            (height * ratio).toInt()
        } else {
            width
        }

        val rectH: Int = if (bitmapWidth > bitmapHeight)  {
            (width * ratio).toInt()
        } else if (bitmapHeight > bitmapWidth) {
            height
        } else {
            width
        }

        val canvasCenter = Point(width / 2, height / 2)

        val left = canvasCenter.x - rectW / 2
        val top = canvasCenter.y - rectH / 2
        val right = canvasCenter.x + rectW / 2
        val bottom = canvasCenter.y + rectH / 2

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
        drawingViewBitmap.drawTransparent()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (event.x.toInt() in boundingRect.left..boundingRect.right && event.y.toInt() in boundingRect.top..boundingRect.bottom) {
                    val coordinateX = ((event.x - boundingRect.left) / scaleWidth).toInt()
                    val coordinateY = ((event.y - boundingRect.top) / scaleHeight).toInt()

                    drawingViewBitmap.setPixel(coordinateX, coordinateY, Color.BLACK)
                    invalidate()
                }
            }
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        if (::drawingViewBitmap.isInitialized) {
            canvas.drawRect(boundingRect, rectPaint)
            canvas.drawBitmap(drawingViewBitmap, null, boundingRect, null)
        }
    }
}