package com.purelab.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.purelab.R
import com.purelab.adapters.CategoryListAdapter
import com.purelab.utils.Category
import com.purelab.utils.toEnumString

class SearchFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val context = context ?: return null
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // リストデータの作成
        val categoryList = Category.values()
        val dataList = categoryList.map{
            it.toEnumString(context)
        }
        // アダプターをセット
        val adapter = CategoryListAdapter(context, dataList)
        val list: ListView = view.findViewById(R.id.list_view) ?: return null
        list.adapter = adapter
        return view
    }
}
