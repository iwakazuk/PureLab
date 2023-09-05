package com.purelab.view.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.purelab.R
import com.purelab.adapters.ItemListAdapter
import com.purelab.databinding.FragmentFavoriteBinding
import com.purelab.view.BaseDataBindingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FavoriteFragment : BaseDataBindingFragment<FragmentFavoriteBinding>() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun getLayoutRes(): Int = R.layout.fragment_favorite
    private val mypageVm: FavoriteViewModel by viewModels()

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
                override fun onItemClick(itemId: String?) {
                    setFragmentResult(
                        "request_key",
                        bundleOf("itemId" to itemId)
                    )
                    findNavController().navigate(
                        R.id.action_itemList_to_dashboardFragment3
                    )
                }
            }
        )

        // ViewModelのdataを観察
        mypageVm.data.observe(viewLifecycleOwner, Observer { data ->
            // アダプターをセット
            adapter.updateData(data)
        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        coroutineScope.launch {
            // 必要に応じてデータを取得
            mypageVm.fetchData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
    }
}
