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
import com.purelab.adapters.ItemListAdapter
import com.purelab.databinding.FragmentNotification1Binding
import com.purelab.models.mockItem

class NotificationFragment1 : BaseDataBindingFragment<FragmentNotification1Binding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_notification1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val context = context ?: return null
        val binding = dataBinding!!

//        setFragmentResultListener("request_key") { _, bundle ->
//            val category = bundle.getString("category")
////            binding.categoryTitle.text = category
//        }

        val itemList = listOf(mockItem())

        // アダプターをセット
        val bookListRecyclerView: RecyclerView = binding.mypageItemListView
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
//    private var count = 0
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val binding = dataBinding!!
//
//        binding.tvCount.text = "Count: $count"
//
//        binding.btnIncrease.setOnClickListener {
//            binding.tvCount.text = "Count: ${++count}"
//        }
//
//        binding.btnNextPage.setOnClickListener {
//            findNavController().navigate(R.id.action_notificationFragment1_to_notificationFragment2)
//        }
//
//        binding.btnFavorites.setOnClickListener {
//            val bundle = bundleOf("count" to count)
//
//            findNavController().navigate(
//                R.id.action_notificationFragment1_to_nav_graph_favorites,
//                bundle
//            )
//        }
//
//    }

}
