package com.purelab.fragment.blankfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.adapters.CategoryListAdapter
import com.purelab.adapters.ItemListAdapter
import com.purelab.databinding.FragmentDashboard2Binding
import com.purelab.models.Item
import com.purelab.models.mockItem
import com.purelab.utils.Category
import com.purelab.utils.toEnumString

class DashboardFragment2 : BaseDataBindingFragment<FragmentDashboard2Binding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_dashboard2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = dataBinding!!

        setFragmentResultListener("request_key") { _, bundle ->
            val category = bundle.getString("category")
            binding.categoryTitle.text = category
        }

        val itemList = listOf(mockItem())

        // アダプターをセット
        val bookListRecyclerView: RecyclerView = binding.itemListView
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        val adapter = ItemListAdapter(itemList)

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
            object : ItemListAdapter.OnItemClickListener {
                override fun onItemClick(itemId: String?) {
                    setFragmentResult(
                        "request_key",
                        bundleOf("item_id" to itemId)
                    )
                    findNavController().navigate(
                        R.id.action_dashboardFragment2_to_dashboardFragment3
                    )
                }
            }
        )

        return binding.root
    }
}
