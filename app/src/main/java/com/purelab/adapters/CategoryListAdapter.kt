package com.purelab.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.utils.Category

class CategoryListAdapter(
    private val categoryListData: MutableList<String>
) : RecyclerView.Adapter<CategoryListAdapter.CategoryListRecyclerViewHolder>() {

    // リスナを格納する変数を定義（インターフェースの型）
    private lateinit var listener: OnItemClickListener

    // クリックイベントリスナのインターフェースを定義
    interface OnItemClickListener {
        fun onItemClick(category: String)
    }

    // リスナをセット
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryListRecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.category_list, parent, false)
        return CategoryListRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryListRecyclerViewHolder, position: Int) {
        val item = categoryListData[position]

        holder.categoryName.text = item

        holder.itemView.setOnClickListener {
            listener.onItemClick(item)
        }
    }

    override fun getItemCount(): Int = categoryListData.size

    class CategoryListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryName: TextView = itemView.findViewById(R.id.category_title)
    }
}
