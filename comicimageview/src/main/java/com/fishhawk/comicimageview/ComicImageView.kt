package com.fishhawk.comicimageview

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap


class ComicImageView : AppCompatImageView {
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context?, attr: AttributeSet?, defStyle: Int)
            : super(context, attr, defStyle)

    private var attacher: ComicImageViewAttacher? = ComicImageViewAttacher(this)
//    private var pendingScaleType: ScaleType? = null
//
//    override fun setScaleType(scaleType: ScaleType) {
//        if (attacher == null) {
//            pendingScaleType = scaleType
//        } else {
//            attacher.setScaleType(scaleType)
//        }
//    }

    init {
        super.setScaleType(ScaleType.MATRIX)
//        pendingScaleType?.let { scaleType = it }
//        pendingScaleType = null
    }

//    var zoomable: Boolean
//        get() = attacher.zoomable
//        set(value) {
//            attacher.zoomable = value
//        }


    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        attacher?.reset(drawable)
    }

    fun setImageDrawableMy(drawable: Drawable?) {
        super.setImageDrawable(drawable)
    }

//    override fun setImageResource(resId: Int) {
//        super.setImageResource(resId)
//        attacher?.reset()
//    }

//    override fun setImageURI(uri: Uri?) {
//        super.setImageURI(uri)
//        attacher?.reset()
//    }
//
//    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
//        val changed = super.setFrame(l, t, r, b)
//        if (changed) attacher?.reset()
//        return changed
//    }
}