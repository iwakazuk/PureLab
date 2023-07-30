package com.purelab.view.itemlist

import android.os.Bundle
import android.view.*
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.purelab.R
import com.purelab.models.Item
import com.purelab.models.mockItem
import com.purelab.repository.ItemRepository
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
        setUpActionBar()

        // リストデータの作成
        val itemList = listOf(mockItem())
//        val dataList: List<ItemData> = itemList.map {
//            // TODO: URLから画像をひっぱってくるようにしたい
//            val itemImage = context.getDrawable(R.drawable.favorite_image)
//            ItemData(
//                itemImage,
//                it.brandName,
//                it.detail
//            )
//        }
//        // アダプターをセット
//        val adapter = ItemListAdapter(context, dataList)
//        val list: ListView = view.findViewById(R.id.item_list_view) ?: return null
//        list.adapter = adapter
        return view
    }

    private fun setUpActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar ?: return
        actionBar.show()
        // アクションバー戻るボタン表示
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu., menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val id = menuItem.itemId
                // 戻るボタンが押された時
                if (id == android.R.id.home) {
                    findNavController().popBackStack()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}