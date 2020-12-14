package com.fishhawk.comicimageview

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import com.fishhawk.comicimageview.databinding.ActivitySimpleConfigSheetBinding
import java.lang.IllegalStateException


class ActivitySimpleConfigSheet(
        context: Context,
        imageView: ComicImageView
) : ConfigSheet(context) {

    private val binding = ActivitySimpleConfigSheetBinding.inflate(
            LayoutInflater.from(context), null, false
    )

    init {
        val scaleType = when (imageView.scaleType) {
            ImageView.ScaleType.FIT_CENTER -> 0
            ImageView.ScaleType.FIT_START -> 1
            ImageView.ScaleType.FIT_END -> 2
            ImageView.ScaleType.FIT_XY -> 3
            ImageView.ScaleType.CENTER -> 4
            ImageView.ScaleType.CENTER_CROP -> 5
            ImageView.ScaleType.CENTER_INSIDE -> 6
            else -> throw IllegalStateException("illegal scale type $imageView.scaleType")
        }
        bind(binding.scaleType, scaleType) { it ->
            when (it) {
                0 -> ImageView.ScaleType.FIT_CENTER
                1 -> ImageView.ScaleType.FIT_START
                2 -> ImageView.ScaleType.FIT_END
                3 -> ImageView.ScaleType.FIT_XY
                4 -> ImageView.ScaleType.CENTER
                5 -> ImageView.ScaleType.CENTER_CROP
                6 -> ImageView.ScaleType.CENTER_INSIDE
                else -> null
            }?.let { imageView.scaleType = it }
        }

        bind(binding.zoomable, imageView.zoomable) {
            imageView.zoomable = it
        }

        bind(binding.opencvEnabled, imageView.isOpenCVEnabled) {
            imageView.isOpenCVEnabled = it
        }

        setContentView(binding.root)
    }
}