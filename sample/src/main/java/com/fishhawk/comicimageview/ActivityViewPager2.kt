package com.fishhawk.comicimageview

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class ActivityViewPager2 : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewpager2)

        viewPager = findViewById<ViewPager2>(R.id.view_pager)
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = SamplePagerAdapter()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_viewpager2, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_ltr -> {
                viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                viewPager.layoutDirection = ViewPager2.LAYOUT_DIRECTION_LTR
                viewPager.adapter = SamplePagerAdapter()
                true
            }
            R.id.action_rtl -> {
                viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                viewPager.layoutDirection = ViewPager2.LAYOUT_DIRECTION_RTL
                viewPager.adapter = SamplePagerAdapter()
                true
            }
            R.id.action_vertical -> {
                viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
                viewPager.adapter = SamplePagerAdapter()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    internal class SamplePagerAdapter : RecyclerView.Adapter<SamplePagerAdapter.ViewHolder>() {
        private val items = intArrayOf(
            R.drawable.pic, R.drawable.pic, R.drawable.pic,
            R.drawable.pic, R.drawable.pic, R.drawable.pic
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_viewpager2, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(position)
        }

        override fun getItemCount(): Int = items.count()

        inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
            fun bind(position: Int) {
                (view as ComicImageView).setImageResource(items[position])
            }
        }
    }
}
