package com.purelab.view.search.ingredient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.databinding.FragmentSearchIngredientBinding
import com.purelab.models.Ingredient
import com.purelab.view.BaseDataBindingFragment
import com.purelab.view.search.SearchTabFragmentDirections

// (SearchFragment)
class SearchIngredientFragment : BaseDataBindingFragment<FragmentSearchIngredientBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_search_ingredient
    private val vm: SearchIngredientViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val context = context ?: return null
        val binding = dataBinding!!

        // ViewModelのdataを観察
        vm.ingredients.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                // アダプターをセット
                setAdapter(binding.listView, data)
            }
        }

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        vm.loadIngredients()
    }

    companion object {
        // フラグメントの新しいインスタンスを生成するファクトリーメソッド
        fun newInstance(): SearchIngredientFragment {
            return SearchIngredientFragment()
        }
    }

    private fun setAdapter(
        recyclerView: RecyclerView,
        ingredients: List<Ingredient>
    ) {
        val dataList: MutableList<Ingredient> = ingredients.toMutableList()
        // アダプターをセット
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        val adapter = IngredientListAdapter(dataList)

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
            object : IngredientListAdapter.OnItemClickListener {
                override fun onItemClick(ingredient: Ingredient) {
                    val action = SearchTabFragmentDirections.actionSearchToItemlist2(ingredient)
                    findNavController().navigate(action)
                }
            }
        )
    }
}
