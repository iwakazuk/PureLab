package com.purelab.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.adapters.CategoryListAdapter
import com.purelab.utils.Category
import com.purelab.utils.FragmentUtils
import com.purelab.utils.toEnumString
import com.purelab.view.itemlist.ItemListFragment

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
        val dataList = categoryList.map {
            it.toEnumString(context)
        }.toMutableList()

        // アダプターをセット
        val bookListRecyclerView = view.findViewById<RecyclerView>(R.id.list_view)
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        val adapter = CategoryListAdapter(dataList)

        bookListRecyclerView.layoutManager = linearLayoutManager
        bookListRecyclerView.adapter = adapter
        bookListRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                linearLayoutManager.orientation
            )
        )

        // CellItem要素クリックイベント
        adapter.setOnItemClickListener(
            // BookListRecyclerViewAdapterで定義した抽象メソッドを実装
            // 再利用をしないため object式でインターフェースを実装
            object : CategoryListAdapter.OnItemClickListener {
                override fun onItemClick(category: String) {
                    // TODO: API実装時画像データも渡せるようにする

                    // データを渡す処理
                    setFragmentResult(
                        "categoryData", bundleOf(
                            "category" to category,
                        )
                    )
                    FragmentUtils.showFragment(
                        ItemListFragment(),
                        parentFragmentManager,
                        R.id.nav_host_fragment
                    )

                }
            }
        )

        setHasOptionsMenu(true)

        return view
    }
}
