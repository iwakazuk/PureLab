package com.purelab.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.purelab.R

class ItemListAdapter(
    context: Context,
    data: List<ItemData>
) : ArrayAdapter<ItemData>(context, 0, data) {
    private lateinit var data: ItemData

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var view = convertView
        if (view == null) {
            // 一行分のレイアウトを生成
            view = LayoutInflater.from(context).inflate(
                R.layout.item_list,
                parent,
                false
            )
        }
        // 一行分のデータを取得
        data = getItem(position) as ItemData
        view?.findViewById<ImageView>(R.id.item_icon)?.apply { data.icon }
        view?.findViewById<TextView>(R.id.item_title)?.apply { text = data.title }
        view?.findViewById<TextView>(R.id.item_text)?.apply { text = data.text }
        return view!!
    }
}
data class ItemData(
    var icon: Drawable? = null,
    var title: String? = null,
    var text: String? = null
)
