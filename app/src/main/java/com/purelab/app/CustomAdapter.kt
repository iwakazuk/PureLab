package com.purelab.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.purelab.R

class CustomAdapter(context: Context, list: ArrayList<Data>) : ArrayAdapter<Data>(context, 0, list) {
    private lateinit var data: Data
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view  = convertView
        if (view == null) {
            // 一行分のレイアウトを生成
            view  = LayoutInflater.from(context).inflate(R.layout.row_view, parent, false)
        }
        // 一行分のデータを取得
        data = getItem(position) as Data
        view?.findViewById<ImageView>(R.id.user_icon).apply {  data.icon }
        view?.findViewById<TextView>(R.id.title)?.apply { text = data.title }
        view?.findViewById<TextView>(R.id.text)?.apply { text = data.text }
        return view!!
    }
}

data class Data(
    var icon: ImageView? = null,
    var title: String? = null,
    var text: String? = null
)