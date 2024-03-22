package com.purelab.view.search.ingredient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.models.Ingredient

class IngredientListAdapter (
    private val ingredientListData: MutableList<Ingredient>
) : RecyclerView.Adapter<IngredientListAdapter.IngredientListRecyclerViewHolder>() {

    // リスナを格納する変数を定義（インターフェースの型）
    private lateinit var listener: OnItemClickListener

    // クリックイベントリスナのインターフェースを定義
    interface OnItemClickListener {
        fun onItemClick(category: Ingredient)
    }

    // リスナをセット
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IngredientListRecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.category_cell, parent, false)
        return IngredientListRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientListRecyclerViewHolder, position: Int) {
        val category = ingredientListData[position]

        holder.ingredientName.text = category.name

        holder.itemView.setOnClickListener {
            listener.onItemClick(category)
        }
    }

    override fun getItemCount(): Int = ingredientListData.size

    class IngredientListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ingredientName: TextView = itemView.findViewById(R.id.category_title)
    }
}
