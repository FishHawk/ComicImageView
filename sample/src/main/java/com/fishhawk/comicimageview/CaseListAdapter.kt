package com.fishhawk.comicimageview

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class CaseListAdapter(
    private val activity: ActicityMain
) : RecyclerView.Adapter<CaseListAdapter.ViewHolder>() {

    private val cases = listOf(
        Case("Simple", ActivitySimple::class.java),
        Case("View Pager", ActivityViewPager::class.java),
        Case("View Pager 2", ActivityViewPager2::class.java)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_case, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cases[position])
    }

    override fun getItemCount(): Int = cases.size

    data class Case(val name: String, val cls: Class<*>)

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(case: Case) {
            val button = view.findViewById<Button>(R.id.button)
            button.text = case.name
            button.setOnClickListener { activity.startActivity(Intent(activity, case.cls)) }
        }
    }
}
