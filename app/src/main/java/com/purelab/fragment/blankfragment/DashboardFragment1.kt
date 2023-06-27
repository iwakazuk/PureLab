package com.purelab.fragment.blankfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.adapters.CategoryListAdapter
import com.purelab.databinding.FragmentDashboard1Binding
import com.purelab.utils.Category
import com.purelab.utils.toEnumString

// (SearchFragment)
class DashboardFragment1 : BaseDataBindingFragment<FragmentDashboard1Binding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_dashboard1

    private var count = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = dataBinding!!

//        binding.tvCount.text = "Count: $count"
//
//        binding.btnIncrease.setOnClickListener {
//            binding.tvCount.text = "Count: ${++count}"
//        }

//        binding.btnNextPage.setOnClickListener {
//            findNavController().navigate(R.id.action_dashboardFragment1_to_dashboardFragment2)
//        }

//        binding.btnGallery.setOnClickListener {
//            val bundle = bundleOf("count" to count)
//
//            findNavController().navigate(
//                R.id.nav_graph_gallery,
//                bundle
//            )
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val context = context ?: return null
        val binding = dataBinding!!

        // リストデータの作成
        val categoryList = Category.values()
        val dataList = categoryList.map {
            it.toEnumString(context)
        }.toMutableList()

        // アダプターをセット
        val bookListRecyclerView:RecyclerView = binding.listView
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
        adapter.setOnItemClickListener(
            // BookListRecyclerViewAdapterで定義した抽象メソッドを実装
            // 再利用をしないため object式でインターフェースを実装
            object : CategoryListAdapter.OnItemClickListener {
                override fun onItemClick(category: String) {
                    findNavController().navigate(R.id.action_dashboardFragment1_to_dashboardFragment2)

                }
            }
        )
        return binding.root
    }
}
