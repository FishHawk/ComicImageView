package com.fishhawk.comicimageview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


class ActivityRecyclerView : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var detector: GestureDetector

    private var isListenerAdded = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)

        recycler = findViewById(R.id.recycler)
        recycler.adapter = SamplePagerAdapter(isListenerAdded)

        detector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                recycler.makeToast("Parent clicked")
                return true
            }

            override fun onLongPress(e: MotionEvent?) {
                recycler.makeToast("Parent long clicked")
            }
        })

        recycler.setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_viewpager2, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_listeners -> {
                isListenerAdded = !isListenerAdded
                recycler.adapter = SamplePagerAdapter(isListenerAdded)
                recycler.makeToast(if (isListenerAdded) "Add listener" else "Remove listener")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    internal class SamplePagerAdapter(
        private val isListenerAdded: Boolean
    ) : RecyclerView.Adapter<SamplePagerAdapter.ViewHolder>() {
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
                (view as ComicImageView).apply {
                    setImageResource(items[position])
                    if (isListenerAdded) {
                        setOnClickListener {
                            makeToast("Clicked")
                        }
                        setOnLongClickListener {
                            makeToast("Long clicked")
                            true
                        }
                    }
                }
            }
        }
    }
}
