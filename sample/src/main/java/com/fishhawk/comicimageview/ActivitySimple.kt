package com.fishhawk.comicimageview

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ActivitySimple : AppCompatActivity() {
    private lateinit var comicImageView: ComicImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)

        comicImageView = findViewById(R.id.image)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_simple, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val scaleType = when (item.itemId) {
            R.id.action_fit_center -> ImageView.ScaleType.FIT_CENTER
            R.id.action_fit_start -> ImageView.ScaleType.FIT_START
            R.id.action_fit_end -> ImageView.ScaleType.FIT_END
            R.id.action_fit_xy -> ImageView.ScaleType.FIT_XY
            R.id.action_center -> ImageView.ScaleType.FIT_CENTER
            R.id.action_center_crop -> ImageView.ScaleType.CENTER_CROP
            R.id.action_center_inside -> ImageView.ScaleType.CENTER_INSIDE
            else -> null
        }
        scaleType?.let {
            comicImageView.scaleType = it
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}