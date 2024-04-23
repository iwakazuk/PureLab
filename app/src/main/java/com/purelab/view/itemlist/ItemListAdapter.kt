package com.purelab.view.itemlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.models.Item
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

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
        holder.image.setImageResource(R.drawable.loading_image)

        Picasso.get()
            .load(item.image)
            .error(R.drawable.home_image) // エラー時のデフォルト画像
            .into(holder.image, object : Callback {
                override fun onSuccess() {}
                override fun onError(e: Exception?) {
                    Log.e("PicassoError", "Failed to load image: " + item.image, e)
                }
            })

        holder.itemView.setOnClickListener {
            listener.onItemClick(item)
        }
    }

    override fun getItemCount(): Int = data.size

    class ItemListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.item_cell_name)
        var brand: TextView = itemView.findViewById(R.id.item_cell_brand)
        var image: ImageView = itemView.findViewById(R.id.item_cell_icon)
    }

    fun updateData(newData: List<Item>) {
        this.data = newData
        notifyDataSetChanged()
    }
}
