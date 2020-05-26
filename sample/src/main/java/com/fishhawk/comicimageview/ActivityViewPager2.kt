package com.fishhawk.comicimageview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class ActivityViewPager2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager2)

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = SamplePagerAdapter()
    }

    internal class SamplePagerAdapter : RecyclerView.Adapter<SamplePagerAdapter.ViewHolder>() {
        private val items = intArrayOf(
            R.drawable.pic, R.drawable.pic, R.drawable.pic,
            R.drawable.pic, R.drawable.pic, R.drawable.pic
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_view_pager2, parent, false)
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
