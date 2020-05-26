package com.fishhawk.comicimageview

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.Matrix.ScaleToFit
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min


private fun Matrix.getScale(): Float {
    val values = FloatArray(9)
    getValues(values)
    val scaleX = values[Matrix.MSCALE_X]
    val scaleY = values[Matrix.MSCALE_Y]
    return (scaleX + scaleY) / 2
}

private fun Matrix.getTranslate(): Pair<Float, Float> {
    val values = FloatArray(9)
    getValues(values)
    val scaleX = values[Matrix.MTRANS_X]
    val scaleY = values[Matrix.MTRANS_Y]
    return Pair(scaleX, scaleY)
}


class ComicImageViewAttacher(private val imageView: ImageView) : View.OnTouchListener,
    View.OnLayoutChangeListener {

    companion object {
        private const val DEFAULT_MAX_SCALE = 2.5f
        private const val DEFAULT_MID_SCALE = 1.5f
        private const val DEFAULT_MIN_SCALE = 1.0f

        private const val HORIZONTAL_EDGE_NONE = -1
        private const val HORIZONTAL_EDGE_LEFT = 0
        private const val HORIZONTAL_EDGE_RIGHT = 1
        private const val HORIZONTAL_EDGE_BOTH = 2

        private const val VERTICAL_EDGE_NONE = -1
        private const val VERTICAL_EDGE_TOP = 0
        private const val VERTICAL_EDGE_BOTTOM = 1
        private const val VERTICAL_EDGE_BOTH = 2

        private fun isSupportedScaleType(scaleType: ScaleType?): Boolean {
            return when (scaleType) {
                null -> false
                ScaleType.MATRIX -> throw IllegalStateException("Matrix scale type is not supported")
                else -> true
            }
        }
    }

    var minScale = DEFAULT_MIN_SCALE
    var midScale = DEFAULT_MID_SCALE
    var maxScale = DEFAULT_MAX_SCALE

    var allowParentInterceptOnHorizontalEdge = true
    var allowParentInterceptOnVerticalEdge = false
    private var mHorizontalScrollEdge = HORIZONTAL_EDGE_BOTH
    private var mVerticalScrollEdge = VERTICAL_EDGE_BOTH


    private var drawable = imageView.drawable
    private var initScale = 1.0f
    private var initTranslateX = 0.0f
    private var initTranslateY = 0.0f
    var scaleType = ScaleType.FIT_CENTER
        set(value) {
            if (isSupportedScaleType(value) && value != field) {
                field = value
                resetLayout()
            }
        }

    private var fixScale = 1.0f
    private val matrix: Matrix = Matrix()

    var zoomable = true


    private var customGestureDetector = CustomGestureDetector(
        imageView.context,
        object : CustomGestureDetector.OnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?, e2: MotionEvent?,
                distanceX: Float, distanceY: Float
            ): Boolean {
                translateImage(-distanceX, -distanceY)
                interceptTouchEventIfNeed(-distanceX, -distanceY)
                return true
            }

            override fun onFling(
                e1: MotionEvent?, e2: MotionEvent?,
                velocityX: Float, velocityY: Float
            ): Boolean {
                startFlingRunnable(-velocityX, -velocityY)
                return true
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleImage(detector.scaleFactor, detector.focusX, detector.focusY)
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {
                val scale = getScale()
                if (scale in minScale..maxScale)
                    moveScaleFromMatrixToBitmapAsync()
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                try {
                    val scale = getScale()
                    val targetScale = when {
                        scale < midScale -> midScale
                        scale >= midScale && scale < maxScale -> maxScale
                        else -> minScale
                    }
                    startScaleRunnable(targetScale)
                } catch (e: ArrayIndexOutOfBoundsException) {
                    // Can sometimes happen when getX() and getY() is called
                }
                return super.onDoubleTap(e)
            }
        })

    private fun interceptTouchEventIfNeed(dx: Float, dy: Float) {
        if (!customGestureDetector.isScaling) {
            val reachHorizontalEdge = mHorizontalScrollEdge == HORIZONTAL_EDGE_BOTH
                    || mHorizontalScrollEdge == HORIZONTAL_EDGE_LEFT && dx >= 1f
                    || mHorizontalScrollEdge == HORIZONTAL_EDGE_RIGHT && dx <= -1f

            val reachVerticalEdge = mVerticalScrollEdge == VERTICAL_EDGE_BOTH
                    || mVerticalScrollEdge == VERTICAL_EDGE_TOP && dy >= 1f
                    || mVerticalScrollEdge == VERTICAL_EDGE_BOTTOM && dy <= -1f

            if ((allowParentInterceptOnHorizontalEdge && reachHorizontalEdge)
                || (allowParentInterceptOnVerticalEdge && reachVerticalEdge)
            ) {
                imageView.parent.requestDisallowInterceptTouchEvent(false)
            }
        } else {
            imageView.parent.requestDisallowInterceptTouchEvent(true)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, ev: MotionEvent): Boolean {
        var handled = false
        if (zoomable) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    cancelFlingRunnable()
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    val scale = getScale()
                    val targetScale = when {
                        scale < minScale -> minScale
                        scale > maxScale -> maxScale
                        else -> null
                    }
                    targetScale?.let {
                        startScaleRunnable(it)
                        handled = true
                    }
                }
            }
            handled = customGestureDetector.onTouchEvent(ev) || handled
        }
        return handled
    }

    override fun onLayoutChange(
        v: View?,
        left: Int, top: Int, right: Int, bottom: Int,
        oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
    ) {
        if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
            resetLayout()
        }
    }

    init {
        imageView.setOnTouchListener(this)
        imageView.addOnLayoutChangeListener(this)
    }

    private var currentFlingRunnable: FlingRunnable? = null

    private fun startFlingRunnable(velocityX: Float, velocityY: Float) {
        correctBound()
        currentFlingRunnable = FlingRunnable(
            imageView,
            getDisplayRect(matrix),
            getImageViewWidth(imageView), getImageViewHeight(imageView),
            velocityX.toInt(), velocityY.toInt()
        ) { dx, dy ->
            translateImage(dx, dy)
        }
        imageView.post(currentFlingRunnable)
    }

    private fun cancelFlingRunnable() {
        currentFlingRunnable?.apply { cancelFling() }
        currentFlingRunnable = null
    }

    private fun startScaleRunnable(targetScale: Float) {
        correctBound()
        val rect = getDisplayRect(matrix)
        rect.let {
            val runnable = ScaleRunnable(
                imageView,
                getScale(), targetScale,
                it.centerX(), it.centerY(),
                { newScale, focalX, focalY ->
                    val deltaScale = newScale / getScale()
                    scaleImage(deltaScale, focalX, focalY)
                },
                { moveScaleFromMatrixToBitmap() }
            )
            imageView.post(runnable)
        }
    }


    private fun translateImage(dx: Float, dy: Float) {
        matrix.postTranslate(dx, dy)
        correctBound()
        applyMatrix()
    }

    private fun scaleImage(scaleFactor: Float, focusX: Float, focusY: Float) {
        if (!scaleFactor.isFinite() || scaleFactor < 0) return
        if (getScale() < maxScale || scaleFactor < 1f) {
            matrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            correctBound()
            applyMatrix()
        }
    }

    private fun getScale(): Float {
        return fixScale * matrix.getScale()
    }

    fun resetDrawable() {
        drawable = imageView.drawable
        resetLayout()
    }

    private fun resetLayout() {
        if (drawable == null) return

        val viewWidth = getImageViewWidth(imageView)
        val viewHeight = getImageViewHeight(imageView)
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight

        if (viewWidth <= 0 || viewHeight <= 0) return
        if (drawableWidth <= 0 || drawableHeight <= 0) return

        val widthScale = viewWidth.toFloat() / drawableWidth
        val heightScale = viewHeight.toFloat() / drawableHeight

        when (scaleType) {
            ScaleType.CENTER -> {
                initScale = 1.0F
                initTranslateX = (viewWidth - drawableWidth) / 2f
                initTranslateY = (viewHeight - drawableHeight) / 2f
            }
            ScaleType.CENTER_CROP -> {
                initScale = max(widthScale, heightScale)
                initTranslateX = (viewWidth - drawableWidth * initScale) / 2f
                initTranslateY = (viewHeight - drawableHeight * initScale) / 2f
            }
            ScaleType.CENTER_INSIDE -> {
                initScale = min(1.0f, min(widthScale, heightScale))
                initTranslateX = (viewWidth - drawableWidth * initScale) / 2f
                initTranslateY = (viewHeight - drawableHeight * initScale) / 2f
            }
            else -> {
                val tempSrc = RectF(0F, 0F, drawableWidth.toFloat(), drawableHeight.toFloat())
                val tempDst = RectF(0F, 0F, viewWidth.toFloat(), viewHeight.toFloat())
                val tempMatrix = Matrix()
                tempMatrix.reset()
                when (scaleType) {
                    ScaleType.FIT_CENTER -> tempMatrix.setRectToRect(
                        tempSrc, tempDst, ScaleToFit.CENTER
                    )
                    ScaleType.FIT_START -> tempMatrix.setRectToRect(
                        tempSrc, tempDst, ScaleToFit.START
                    )
                    ScaleType.FIT_END -> tempMatrix.setRectToRect(
                        tempSrc, tempDst, ScaleToFit.END
                    )
                    ScaleType.FIT_XY -> tempMatrix.setRectToRect(
                        tempSrc, tempDst, ScaleToFit.FILL
                    )
                    else -> {
                    }
                }
                initScale = tempMatrix.getScale()
                val (tx, ty) = tempMatrix.getTranslate()
                initTranslateX = tx
                initTranslateY = ty
            }
        }
        matrix.reset()
        matrix.setTranslate(initTranslateX, initTranslateY)
        moveScaleFromMatrixToBitmap()
    }


    /*
    * change ImageView
    */
    private fun moveScaleFromMatrixToBitmap() {
        val scale = matrix.getScale()
        val bitmap = ScaleAlgorithm.scale(drawable.toBitmap(), initScale * fixScale * scale)
        val newD = BitmapDrawable(imageView.context.resources, bitmap)
        (imageView as ComicImageView).setImageDrawableMy(newD)

        val (tx, ty) = matrix.getTranslate()
        fixScale *= scale
        matrix.reset()
        matrix.setTranslate(tx, ty)
        applyMatrix()
    }

    private fun moveScaleFromMatrixToBitmapAsync() {
        val scale = matrix.getScale()

        GlobalScope.launch(Dispatchers.Default) {
            val bitmap = ScaleAlgorithm.scale(drawable.toBitmap(), initScale * fixScale * scale)
            withContext(Dispatchers.Main) {
                val newD = BitmapDrawable(imageView.context.resources, bitmap)
                (imageView as ComicImageView).setImageDrawableMy(newD)

                val (tx, ty) = matrix.getTranslate()
                fixScale *= scale
                matrix.reset()
                matrix.setTranslate(tx, ty)
                applyMatrix()
            }
        }

    }

    private fun applyMatrix() {
        imageView.imageMatrix = matrix
    }

    private fun getDisplayRect(matrix: Matrix): RectF {
        val d: Drawable = imageView.drawable
        val rect = RectF(0F, 0F, d.intrinsicWidth.toFloat(), d.intrinsicHeight.toFloat())
        matrix.mapRect(rect)
        return rect
    }

    private fun correctBound(): Boolean {
        val rect: RectF = getDisplayRect(matrix)
        val height = rect.height()
        val width = rect.width()
        var deltaX = 0f
        var deltaY = 0f
        val viewHeight = getImageViewHeight(imageView)
        when {
            height <= viewHeight -> {
                deltaY = when (scaleType) {
                    ScaleType.FIT_START -> -rect.top
                    ScaleType.FIT_END -> viewHeight - height - rect.top
                    else -> (viewHeight - height) / 2 - rect.top
                }
                mVerticalScrollEdge = VERTICAL_EDGE_BOTH
            }
            rect.top > 0 -> {
                mVerticalScrollEdge = VERTICAL_EDGE_TOP
                deltaY = -rect.top
            }
            rect.bottom < viewHeight -> {
                mVerticalScrollEdge = VERTICAL_EDGE_BOTTOM
                deltaY = viewHeight - rect.bottom
            }
            else -> {
                mVerticalScrollEdge = VERTICAL_EDGE_NONE
            }
        }
        val viewWidth = getImageViewWidth(imageView)
        when {
            width <= viewWidth -> {
                mHorizontalScrollEdge = HORIZONTAL_EDGE_BOTH
                deltaX = when (scaleType) {
                    ScaleType.FIT_START -> -rect.left
                    ScaleType.FIT_END -> viewWidth - width - rect.left
                    else -> (viewWidth - width) / 2 - rect.left
                }
            }
            rect.left > 0 -> {
                mHorizontalScrollEdge = HORIZONTAL_EDGE_LEFT
                deltaX = -rect.left
            }
            rect.right < viewWidth -> {
                deltaX = viewWidth - rect.right
                mHorizontalScrollEdge = HORIZONTAL_EDGE_RIGHT
            }
            else -> {
                mHorizontalScrollEdge = HORIZONTAL_EDGE_NONE
            }
        }
        // Finally actually translate the matrix
        matrix.postTranslate(deltaX, deltaY)
        return true
    }


    private fun getImageViewWidth(imageView: ImageView): Int {
        return imageView.width - imageView.paddingLeft - imageView.paddingRight
    }

    private fun getImageViewHeight(imageView: ImageView): Int {
        return imageView.height - imageView.paddingTop - imageView.paddingBottom
    }
}