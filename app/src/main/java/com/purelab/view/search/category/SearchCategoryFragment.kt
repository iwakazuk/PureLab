package com.purelab.view.search.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.adapters.CategoryListAdapter
import com.purelab.databinding.FragmentSearchCategoryBinding
import com.purelab.enums.Category
import com.purelab.view.BaseDataBindingFragment
import com.purelab.utils.toEnumString
import com.purelab.view.search.SearchTabFragmentDirections

// (SearchFragment)
class SearchCategoryFragment : BaseDataBindingFragment<FragmentSearchCategoryBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_search_category

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val context = context ?: return null
        val binding = dataBinding!!

        // リストデータの作成
        val categoryList = Category.values()
        val dataList = categoryList.map {
            it.toEnumString(context)
        }.toMutableList()

        // アダプターをセット
        val bookListRecyclerView:RecyclerView = binding.listView
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
            object : CategoryListAdapter.OnItemClickListener {
                override fun onItemClick(category: String) {
                    val action = SearchTabFragmentDirections.actionSearchToItemlist(category)
                    findNavController().navigate(action)
                }
            }
        )
        return binding.root
    }

    companion object {
        // フラグメントの新しいインスタンスを生成するファクトリーメソッド
        fun newInstance(): SearchCategoryFragment {
            return SearchCategoryFragment()
        }
    }
}
