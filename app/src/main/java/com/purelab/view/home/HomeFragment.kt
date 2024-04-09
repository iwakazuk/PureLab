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
import com.purelab.app.ViewModelFactory
import com.purelab.databinding.FragmentHomeBinding
import com.purelab.models.Item
import com.purelab.repository.RealmRepository
import com.purelab.view.BaseDataBindingFragment
import com.purelab.view.mypage.favorite.FavoriteViewModel
import kotlinx.coroutines.*

/** ホーム画面 */
class HomeFragment : BaseDataBindingFragment<FragmentHomeBinding>() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun getLayoutRes(): Int = R.layout.fragment_home

    private val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(
            requireActivity().application,
            RealmRepository()
        )
    }
    private val vm: HomeViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = dataBinding!!

        // ViewModelのdataを観察
        vm.newResult.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                // アダプターをセット
                setAdapter(binding.newItemCardListView, data)
                setAdapter(binding.recommendItemCardListView, data)
            }
        }

        // プルリフレッシュのリスナーを設定
        binding.swipeRefreshLayout.setOnRefreshListener {
            vm.fetchNewItems()
        }

        vm.isLoaded.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }


        // RecyclerViewの設定
        val recyclerView: RecyclerView = binding.mydataIngredientListView
        recyclerView.layoutManager = LinearLayoutManager(context)

        // ViewModelのdataを観察
        vm.favoriteResults.observe(viewLifecycleOwner) { data ->
            val progressList = data?.mapNotNull { it.ingredients?.filterNotNull() }
                ?.filter { it.isNotEmpty() }
                ?.flatten()
                ?.groupingBy { it }
                ?.eachCount()
                ?.map { Pair(it.key, it.value * 10) }
                ?: return@observe // dataがnullの場合はここでリターン

            val adapter = MyDataIngredientListAdapter(progressList)
            recyclerView.adapter = adapter
        }


        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // 最新アイテムを取得
        vm.fetchNewItems()
    }


    override fun onResume() {
        super.onResume()
        coroutineScope.launch {
            // 必要に応じてデータを取得
            vm.loadFavorite()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
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
