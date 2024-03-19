package com.purelab.view.itemlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.models.Item

class ItemListAdapter(
    private var data: List<Item>
) : RecyclerView.Adapter<ItemListAdapter.ItemListRecyclerViewHolder>() {
    // リスナを格納する変数を定義（インターフェースの型）
    private lateinit var listener: OnItemClickListener

    // クリックイベントリスナのインターフェースを定義
    interface OnItemClickListener {
        fun onItemClick(item: Item?)
    }

    // リスナをセット
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemListRecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_cell, parent, false)
        return ItemListRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ItemListRecyclerViewHolder,
        position: Int
    ) {
        val item = data[position]
        holder.name.text = item.name
        holder.brand.text = item.brand

        holder.itemView.setOnClickListener {
            listener.onItemClick(item)
        }
    }

    override fun getItemCount(): Int = data.size

    class ItemListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.item_cell_name)
        var brand: TextView = itemView.findViewById(R.id.item_cell_brand)
    }

    fun updateData(newData: List<Item>) {
        this.data = newData
        notifyDataSetChanged()
    }
}
