package com.fishhawk.comicimageview

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


class ComicImageView : AppCompatImageView {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context, attr: AttributeSet?, defStyle: Int)
            : super(context, attr, defStyle)

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        attacher?.resetDrawable()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        attacher?.resetDrawable()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        attacher?.resetDrawable()
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        val changed = super.setFrame(l, t, r, b)
        if (changed) attacher?.resetDrawable()
        return changed
    }

    override fun setScaleType(scaleType: ScaleType) {
        if (attacher == null) {
            pendingScaleType = scaleType
        } else {
            attacher.scaleType = scaleType
        }
    }

    override fun getScaleType(): ScaleType {
        return attacher.scaleType
    }


    private var attacher: ComicImageViewAttacher = ComicImageViewAttacher(this)
    private var pendingScaleType: ScaleType? = null

    init {
        super.setScaleType(ScaleType.MATRIX)
        pendingScaleType?.let {
            attacher.scaleType = scaleType
        }
        pendingScaleType = null
    }


    fun setImageDrawableMy(drawable: Drawable?) {
        super.setImageDrawable(drawable)
    }


    var zoomable = attacher.zoomable
    var isBetterScaleAlgorithmEnabled = attacher.isBetterScaleAlgorithmEnabled

    var minimumScale = attacher.minScale
    var mediumScale = attacher.midScale
    var maximumScale = attacher.maxScale

    fun setScaleLevels(minimumScale: Float, mediumScale: Float, maximumScale: Float) {
        attacher.minScale = minimumScale
        attacher.midScale = mediumScale
        attacher.maxScale = maximumScale
    }

    var allowParentInterceptOnHorizontalEdge = attacher.allowParentInterceptOnHorizontalEdge
    var allowParentInterceptOnVerticalEdge = attacher.allowParentInterceptOnVerticalEdge

    var onScaleListener: OnScaleListener?
        get() = attacher.onScaleListener
        set(value) {
            attacher.onScaleListener = value
        }
    var onFlingListener: OnFlingListener?
        get() = attacher.onFlingListener
        set(value) {
            attacher.onFlingListener = value
        }
    var onDragListener: com.fishhawk.comicimageview.OnDragListener?
        get() = attacher.onDragListener
        set(value) {
            attacher.onDragListener = value
        }
    var onTapListener: OnTapListener?
        get() = attacher.onTapListener
        set(value) {
            attacher.onTapListener = value
        }

    override fun setOnClickListener(l: OnClickListener?) {
        attacher.onClickListener = l
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        attacher.onLongClickListener = l
    }
}