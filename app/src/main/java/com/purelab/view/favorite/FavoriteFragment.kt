package com.purelab.view.favorite

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
import com.purelab.R
import com.purelab.view.itemlist.ItemListAdapter
import com.purelab.databinding.FragmentFavoriteBinding
import com.purelab.models.Item
import com.purelab.repository.RealmRepository
import com.purelab.view.BaseDataBindingFragment
import com.purelab.app.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FavoriteFragment : BaseDataBindingFragment<FragmentFavoriteBinding>() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun getLayoutRes(): Int = R.layout.fragment_favorite

    private val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(
            requireActivity().application,
            RealmRepository()
        )
    }

    private val favoriteVm: FavoriteViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = dataBinding!!

        // アダプターをセット
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        val adapter = ItemListAdapter(emptyList())

        val recyclerView = binding.mypageItemListView
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
            object : ItemListAdapter.OnItemClickListener {
                override fun onItemClick(item: Item?) {
                    setFragmentResult(
                        "request_key",
                        bundleOf("item" to item)
                    )
                    findNavController().navigate(
                        R.id.action_itemList_to_itemDetail
                    )
                }
            }
        )

        // ViewModelのdataを観察
        favoriteVm.data.observe(viewLifecycleOwner) { data ->
            // アダプターをセット
            adapter.updateData(data)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        coroutineScope.launch {
            // 必要に応じてデータを取得
            favoriteVm.loadFavorite()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
    }
}
