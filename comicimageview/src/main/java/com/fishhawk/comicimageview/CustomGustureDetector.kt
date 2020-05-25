package com.fishhawk.comicimageview

import android.content.Context
import android.view.*
import android.view.ScaleGestureDetector.OnScaleGestureListener


internal class CustomGestureDetector(
    context: Context?,
    listener: OnGestureListener
) {
    private val scaleDetector: ScaleGestureDetector = ScaleGestureDetector(context, listener)
    private val normalDetector = GestureDetector(context, listener)

    var isDragging = false
        private set
    val isScaling: Boolean
        get() = scaleDetector.isInProgress

    fun onTouchEvent(ev: MotionEvent): Boolean {
        return try {
            scaleDetector.onTouchEvent(ev)
            normalDetector.onTouchEvent(ev)
            true
        } catch (e: IllegalArgumentException) {
            // Fix for support lib bug, happening when onDestroy is called
            true
        }
    }

    abstract class OnGestureListener :
        GestureDetector.SimpleOnGestureListener(),
        OnScaleGestureListener {
        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean = true
        override fun onScaleEnd(detector: ScaleGestureDetector?) {}
    }
}
