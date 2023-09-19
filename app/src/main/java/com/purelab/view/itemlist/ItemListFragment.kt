package com.purelab.view.itemlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.adapters.ItemListAdapter
import com.purelab.databinding.FragmentItemlistBinding
import com.purelab.view.BaseDataBindingFragment
import com.purelab.models.mockItem
import com.purelab.view.favorite.FavoriteViewModel

class ItemListFragment : BaseDataBindingFragment<FragmentItemlistBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_itemlist
    private val vm: ItemListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = dataBinding!!

        setFragmentResultListener("request_key") { _, bundle ->
            val category = bundle.getString("category") ?: return@setFragmentResultListener
            vm.setCategory(category)
        }

        vm.category.observe(viewLifecycleOwner) { category ->
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
                        bundleOf("itemId" to itemId)
                    )
                    findNavController().navigate(
                        R.id.action_itemList_to_itemDetail
                    )
                }
            }
        )

        return binding.root
    }
}
