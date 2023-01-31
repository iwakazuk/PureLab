package com.purelab.view.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.purelab.R
import com.purelab.adapters.ItemData
import com.purelab.adapters.ItemListAdapter
import com.purelab.utils.toEnumString

class ItemListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val context = context ?: return null
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // リストデータの作成
        val itemList = Item.values()
        val dataList = itemList.map {
            ItemData(
                it.icon.toEnumString(context),
                it.title.toEnumString(context),
                it.text.toEnumString(context)
            )
        }
        // アダプターをセット
        val adapter = ItemListAdapter(context, dataList)
        val list: ListView = view.findViewById(R.id.item_list_view) ?: return null
        list.adapter = adapter
        return view
    }
}