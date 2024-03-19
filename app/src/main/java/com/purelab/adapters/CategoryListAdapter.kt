package com.purelab.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.models.Category

class CategoryListAdapter(
    private val categoryListData: MutableList<Category>
) : RecyclerView.Adapter<CategoryListAdapter.CategoryListRecyclerViewHolder>() {

    // リスナを格納する変数を定義（インターフェースの型）
    private lateinit var listener: OnItemClickListener

    // クリックイベントリスナのインターフェースを定義
    interface OnItemClickListener {
        fun onItemClick(category: Category)
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
        val view = inflater.inflate(R.layout.category_cell, parent, false)
        return CategoryListRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryListRecyclerViewHolder, position: Int) {
        val category = categoryListData[position]

        holder.categoryName.text = category.name

        holder.itemView.setOnClickListener {
            listener.onItemClick(category)
        }
    }

    override fun getItemCount(): Int = categoryListData.size

    class CategoryListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryName: TextView = itemView.findViewById(R.id.category_title)
    }
}
