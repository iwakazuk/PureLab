package com.purelab.fragment.blankfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.purelab.R
import com.purelab.adapters.CategoryListAdapter
import com.purelab.adapters.ItemCardListAdapter
import com.purelab.databinding.FragmentHome1Binding
import com.purelab.models.Item
import com.purelab.models.mockItem

class HomeFragment1 : BaseDataBindingFragment<FragmentHome1Binding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_home1
    private var itemList: List<Item> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        val binding = dataBinding!!
        itemList = listOf(mockItem(), mockItem(), mockItem(), mockItem(), mockItem(), mockItem())

        // アダプターをセット
        val bookListRecyclerView: RecyclerView = binding.itemCardListView
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        val adapter = ItemCardListAdapter(itemList)

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

            object : ItemCardListAdapter.OnItemClickListener {
                override fun onItemClick(itemId: String?) {
                    setFragmentResult(
                        "request_key",
                        bundleOf("itemId" to itemId)
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
