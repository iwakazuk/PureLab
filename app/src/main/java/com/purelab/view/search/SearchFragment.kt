package com.purelab.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.purelab.R
import com.purelab.adapters.ListData
import com.purelab.adapters.CategoryListAdapter

class SearchFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // リストデータの作成
        val dataList = arrayListOf<ListData>()
        for (i in 0..25) {
            dataList.add(ListData().apply {
                title = "${i}番目のタイトル"
            })
        }
        // アダプターをセット
        val adapter = context?.let { CategoryListAdapter(it, dataList) }
        val list: ListView = view?.findViewById(R.id.list_view) ?: return null
        list.adapter = adapter
        return inflater.inflate(R.layout.fragment_search, container, false)
    }
}
