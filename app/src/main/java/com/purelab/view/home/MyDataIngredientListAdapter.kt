package com.purelab.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R

/** ホーム-マイデータ(成分バー用)アダプタ */
class MyDataIngredientListAdapter(private val progressList: List<Pair<String, Int>>) : RecyclerView.Adapter<MyDataIngredientListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.mydata_title)
        val progressBar: ProgressBar = view.findViewById(R.id.mydata_progress_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.mydata_progress_bar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = progressList[position]
        holder.title.text = item.first
        holder.progressBar.progress = item.second
    }

    override fun getItemCount(): Int = progressList.size
}