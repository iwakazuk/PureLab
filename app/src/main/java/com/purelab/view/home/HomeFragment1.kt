package com.purelab.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.adapters.ItemCardListAdapter
import com.purelab.adapters.MyDataIngredientListAdapter
import com.purelab.databinding.FragmentHome1Binding
import com.purelab.models.Item
import com.purelab.view.BaseDataBindingFragment


class HomeFragment1 : BaseDataBindingFragment<FragmentHome1Binding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_home1
    private val homeVm: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = dataBinding!!

        // ViewModelのdataを観察
        homeVm.newResult.observe(viewLifecycleOwner) { data ->
            // アダプターをセット
            setAdapter(binding.newItemCardListView, data)
            setAdapter(binding.recommendItemCardListView, data)
        }

        // プルリフレッシュのリスナーを設定
        binding.swipeRefreshLayout.setOnRefreshListener {
            homeVm.fetchNewItems()
        }

        homeVm.isLoaded.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }

        // リストデータの準備（例：0から100までのランダムな値）
        val progressList = List(5) { (0..100).random() }

        // RecyclerViewの設定
        val recyclerView: RecyclerView = binding.mydataIngredientListView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = MyDataIngredientListAdapter(progressList)
        recyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // 最新アイテムを取得
        homeVm.fetchNewItems()
    }

    private fun setAdapter(
        recyclerView: RecyclerView,
        itemList: List<Item>
    ) {
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        val adapter = ItemCardListAdapter(itemList)

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
            // BookListRecyclerViewAdapterで定義した抽象メソッドを実装
            // 再利用をしないため object式でインターフェースを実装
            object : ItemCardListAdapter.OnItemClickListener {
                override fun onItemClick(itemId: String?) {
                    setFragmentResult(
                        "request_key",
                        bundleOf("itemId" to itemId)
                    )
                    findNavController().navigate(
                        R.id.action_homeFragment1_to_homeFragment2
                    )
                }
            }
        )
    }
}
