package com.purelab.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.models.Item

class ItemCardListAdapter(
    private val data: List<Item>
) : RecyclerView.Adapter<ItemCardListAdapter.ItemCardListRecyclerViewHolder>() {
    // リスナを格納する変数を定義（インターフェースの型）
    private lateinit var listener: OnItemClickListener

    // クリックイベントリスナのインターフェースを定義
    interface OnItemClickListener {
        fun onItemClick(itemId: String?)
    }

    // リスナをセット
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemCardListRecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_card, parent, false)
        return ItemCardListRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ItemCardListRecyclerViewHolder,
        position: Int
    ) {
        val item = data[position]
        // holder.icon.text = item.itemId
        holder.name.text = item.name
        holder.category.text = item.category
        holder.brand.text = item.brand
        holder.detail.text = item.description

        holder.itemView.setOnClickListener {
            listener.onItemClick(item.id)
        }
    }

    override fun getItemCount(): Int = data.size

    class ItemCardListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: ImageView = itemView.findViewById(R.id.item_card_icon)
        var name: TextView = itemView.findViewById(R.id.item_card_title)
        var category: TextView = itemView.findViewById(R.id.item_card_category)
        var brand: TextView = itemView.findViewById(R.id.item_card_brand)
        var detail: TextView = itemView.findViewById(R.id.item_card_text)
    }
}
