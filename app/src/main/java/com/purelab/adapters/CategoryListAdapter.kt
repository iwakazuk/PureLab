package com.purelab.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.purelab.R

class CategoryListAdapter(
    context: Context,
    list: ArrayList<ListData>
) : ArrayAdapter<ListData>(context, 0, list) {
    private lateinit var data: ListData

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var view  = convertView
        if (view == null) {
            // 一行分のレイアウトを生成
            view  = LayoutInflater.from(context).inflate(
                R.layout.category_list,
                parent,
                false
            )
        }
        // 一行分のデータを取得
        data = getItem(position) as ListData
        view?.findViewById<TextView>(R.id.category_title)?.apply { text = data.title }
        return view!!
    }
}

data class ListData(
    var icon: ImageView? = null,
    var title: String? = null,
    var text: String? = null
)