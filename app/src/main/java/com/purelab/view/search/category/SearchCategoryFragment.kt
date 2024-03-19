package com.purelab.view.search.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.adapters.CategoryListAdapter
import com.purelab.databinding.FragmentSearchCategoryBinding
import com.purelab.models.Category
import com.purelab.view.BaseDataBindingFragment
import com.purelab.view.search.SearchTabFragmentDirections

// (SearchFragment)
class SearchCategoryFragment : BaseDataBindingFragment<FragmentSearchCategoryBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_search_category
    private val vm: SearchCategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val context = context ?: return null
        val binding = dataBinding!!

        // ViewModelのdataを観察
        vm.categories.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                // アダプターをセット
                setAdapter(binding.listView, data)
            }
        }

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        vm.fetchCategories()
    }

    companion object {
        // フラグメントの新しいインスタンスを生成するファクトリーメソッド
        fun newInstance(): SearchCategoryFragment {
            return SearchCategoryFragment()
        }
    }

    private fun setAdapter(
        recyclerView: RecyclerView,
        categories: List<Category>
    ) {
        val dataList: MutableList<Category> = categories.toMutableList()
        // アダプターをセット
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        val adapter = CategoryListAdapter(dataList)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                linearLayoutManager.orientation
            )
        )

        // CellItem要素クリックイベント
        adapter.setOnItemClickListener(
            object : CategoryListAdapter.OnItemClickListener {
                override fun onItemClick(category: Category) {
                    val action = SearchTabFragmentDirections.actionSearchToItemlist(category)
                    findNavController().navigate(action)
                }
            }
        )
    }
}
