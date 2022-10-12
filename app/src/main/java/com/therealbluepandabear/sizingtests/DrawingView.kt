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

    private var bitmapWidth = 1
    private var bitmapHeight = 150

    private var clipBoundsRect = Rect()

    private var dx = 0f
    private var dy = 0f

    private var originalX: Float? = null
    private var originalY: Float? = null

    private var moveMode = true

    private var currentZoom = 1f

    private var canvasX = 0f
    private var canvasY = 0f

    object PaintData {
        val rectPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.WHITE
            setShadowLayer(10f,0f, 0f, Color.argb(100, 0, 0, 0))
        }

        var gridPaint = Paint().apply {
            strokeWidth = 1f
            pathEffect = null
            color = Color.BLACK
            style = Paint.Style.STROKE
            isDither = true
            isAntiAlias = true
            isFilterBitmap = false
        }
    }

    companion object {
        const val ZOOM_FACTOR = 0.2f
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

    fun toggleMoveMode() {
        moveMode = !moveMode
    }

    fun zoomIn() {
        currentZoom += ZOOM_FACTOR
        invalidate()
    }

    fun zoomOut() {
        currentZoom -= ZOOM_FACTOR
        invalidate()
    }

    private fun drawGrid(canvas: Canvas) {
        var xm = (boundingRect.top).toFloat()

        for (i in 0 .. bitmapHeight) {
            canvas.drawLine(
                (boundingRect.left).toFloat(),
                xm,
                (boundingRect.right).toFloat(),
                xm,
                PaintData.gridPaint
            )

            xm += scaleHeight
        }

        var ym = (boundingRect.left).toFloat()

        for (i in 0 .. bitmapWidth) {
            canvas.drawLine(
                ym,
                (boundingRect.top).toFloat(),
                ym,
                (boundingRect.bottom).toFloat(),
                PaintData.gridPaint
            )

            ym += scaleWidth
        }
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

    private fun doOnTouchEvent(event: MotionEvent) {
        val x: Float = event.x / currentZoom + clipBoundsRect.left
        val y: Float = event.y / currentZoom + clipBoundsRect.top

        val coordinateX = ((x - boundingRect.left) / scaleWidth).toInt()
        val coordinateY = ((y - boundingRect.top) / scaleHeight).toInt()

        if (coordinateX in 0 until drawingViewBitmap.width && coordinateY in 0 until drawingViewBitmap.height) {
            drawingViewBitmap.setPixel(coordinateX, coordinateY, Color.BLACK)
            invalidate()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (moveMode) {
                    if (originalX == null && originalY == null) {
                        originalX = canvasX
                        originalY = canvasY
                    }

                    dx = canvasX - event.rawX
                    dy = canvasY - event.rawY
                } else {
                    doOnTouchEvent(event)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (moveMode) {
                    canvasX = event.rawX + dx
                    canvasY = event.rawY + dy
                    invalidate()
                } else {
                    doOnTouchEvent(event)
                }
            }

            MotionEvent.ACTION_UP -> {
                drawingViewBitmap.eraseColor(Color.TRANSPARENT)
                invalidate()
            }
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        if (::drawingViewBitmap.isInitialized) {
            canvas.save()
            canvas.translate(canvasX, canvasY)
            canvas.scale(currentZoom, currentZoom, width / 2f, height / 2f)
            canvas.getClipBounds(clipBoundsRect)
            canvas.drawRect(boundingRect, PaintData.rectPaint)
            canvas.drawBitmap(drawingViewBitmap, null, boundingRect, null)
            drawGrid(canvas)
            canvas.restore()
        }
    }
}