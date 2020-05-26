package com.fishhawk.comicimageview

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


class ComicImageView : AppCompatImageView {
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context?, attr: AttributeSet?, defStyle: Int)
            : super(context, attr, defStyle)

    private var attacher: ComicImageViewAttacher? = ComicImageViewAttacher(this)
    private var pendingScaleType: ScaleType? = null

    override fun setScaleType(scaleType: ScaleType) {
        if (attacher == null) {
            pendingScaleType = scaleType
        } else {
            attacher!!.scaleType = scaleType
        }
    }

    override fun getScaleType(): ScaleType {
        return attacher!!.scaleType
    }

    init {
        super.setScaleType(ScaleType.MATRIX)
        pendingScaleType?.let {
            attacher!!.scaleType = scaleType
        }
        pendingScaleType = null
    }

    var zoomable: Boolean
        get() = attacher!!.zoomable
        set(value) {
            attacher!!.zoomable = value
        }


    fun setImageDrawableMy(drawable: Drawable?) {
        super.setImageDrawable(drawable)
    }

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
}