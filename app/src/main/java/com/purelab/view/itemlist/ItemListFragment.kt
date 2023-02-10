package com.purelab.view.itemlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.purelab.R
import com.purelab.adapters.ItemData
import com.purelab.adapters.ItemListAdapter
import com.purelab.models.Item
import com.purelab.models.mockItem
import com.purelab.repository.ItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemListFragment : Fragment() {
    private val itemRepository = ItemRepository()
    var list = listOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            list = itemRepository.getItem()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val context = context ?: return null
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // リストデータの作成
        val itemList = listOf(mockItem())
        val dataList: List<ItemData> = itemList.map {
            // TODO: URLから画像をひっぱってくるようにしたい
            val itemImage = context.getDrawable(R.drawable.favorite_image)
            ItemData(
                itemImage,
                it.name,
                it.detail
            )
        }
        // アダプターをセット
        val adapter = ItemListAdapter(context, dataList)
        val list: ListView = view.findViewById(R.id.item_list_view) ?: return null
        list.adapter = adapter
        return view
    }
}