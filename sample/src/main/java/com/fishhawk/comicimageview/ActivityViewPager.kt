package com.fishhawk.comicimageview

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager


class ActivityViewPager : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewpager)

        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = SamplePagerAdapter()
    }

    internal class SamplePagerAdapter : PagerAdapter() {
        private val items = intArrayOf(
            R.drawable.pic, R.drawable.pic, R.drawable.pic,
            R.drawable.pic, R.drawable.pic, R.drawable.pic
        )

        override fun getCount(): Int = items.size

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = ComicImageView(container.context)
            photoView.setImageResource(items[position])

            container.addView(
                photoView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }
}
