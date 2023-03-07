package com.purelab.view.bottom.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.platform.Hold
import com.purelab.R
import com.purelab.adapters.CategoryListAdapter
import com.purelab.enums.NavComponent
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
        adapter.setOnItemClickListener{ view, component ->
            selectedComponent(view, component)
        }
        return view
    }

    private fun selectedComponent(view: View, component: NavComponent) {
        exitTransition = Hold()
        reenterTransition = null

        val extras = FragmentNavigatorExtras(view to view.transitionName)
        findNavController().navigate(component.navigationId, null, null, extras)
    }

    private val NavComponent.navigationId: Int
        get() = when (this) {
            NavComponent.BOTTOM_NAVIGATION -> R.id.nav_itemList
        }
}
