package com.purelab.view.itemlist

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
import com.purelab.databinding.FragmentItemlistBinding
import com.purelab.models.Category
import com.purelab.models.Ingredient
import com.purelab.models.Item
import com.purelab.view.BaseDataBindingFragment

/** 商品一覧画面 */
class ItemListFragment : BaseDataBindingFragment<FragmentItemlistBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_itemlist
    private val vm: ItemListViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val category: Category? = arguments?.getParcelable("category")
        val ingredient: Ingredient? = arguments?.getParcelable("ingredient")

        if (category != null) {
            // ViewModelへのカテゴリ設定
            vm.isCategory = true
            vm.setCategory(category)
            vm.fetchItemsByCategory()
        } else if (ingredient != null) {
            // ViewModelへのカテゴリ設定
            vm.isCategory = false
            vm.setIngredient(ingredient)
            vm.fetchItemsByIngredient()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = dataBinding!!

        vm.category.observe(viewLifecycleOwner) { category ->
            if (vm.isCategory) {
                binding.categoryTitle.text = category.name
            }
        }
        vm.ingredient.observe(viewLifecycleOwner) { ingredient ->
            if (!vm.isCategory) {
                binding.categoryTitle.text = ingredient.name
            }
        }

        vm.items.observe(viewLifecycleOwner) { itemList ->
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

            adapter.setOnItemClickListener(
                object : ItemListAdapter.OnItemClickListener {
                    override fun onItemClick(item: Item?) {
                        item?.let {
                            val action = ItemListFragmentDirections.actionItemListToItemDetail(it)
                            findNavController().navigate(action)
                        }
                    }
                }
            )
        }

        return binding.root
    }
}
