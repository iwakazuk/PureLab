package com.purelab.view.admin

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.purelab.R
import com.purelab.databinding.FragmentAdminBinding
import com.purelab.utils.CustomSnackbar
import com.purelab.utils.hideKeyboard
import com.purelab.view.BaseDataBindingFragment

class AdminFragment : BaseDataBindingFragment<FragmentAdminBinding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_admin
    private val vm: AdminViewModel by viewModels()
    private lateinit var binding: FragmentAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentAdminBinding.inflate(inflater, container, false)

        // ブランドのピッカーを設定
        vm.loadBrands()
        vm.brands.observe(viewLifecycleOwner) { brandList ->
            val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, brandList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.adminBrand.adapter = adapter
        }

        // カテゴリのピッカーを設定
        vm.loadCategories()
        vm.categories.observe(viewLifecycleOwner) { categoryList ->
            val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, categoryList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.adminCategory.adapter = adapter
        }

        // 成分のピッカーを設定
        vm.loadIngredients()
        vm.selectedIngredients.observe(viewLifecycleOwner) { ingredients ->
            val text = if (ingredients.isEmpty()) {
                "成分を選択"
            } else {
                ingredients.joinToString(separator = "\n")
            }
            binding.adminIngredientsLabel.text = text
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHideKeyBoard(binding.root)

        binding.saveButton.setOnClickListener { v ->
            val product = hashMapOf(
                "name" to binding.adminName.text,
                "brandID" to binding.adminBrand.textAlignment,
                "categoryID" to binding.adminCategory.textAlignment,
                "ingredientIDs" to listOf(binding.adminIngredients)
            )
            vm.saveItem(product)

            // 保存完了のsnackbarを表示
            CustomSnackbar.showSnackBar(view, "商品を保存しました")
        }

        binding.adminIngredients.setOnClickListener { v ->
            val ingredients = vm.ingredients.value?.toTypedArray() ?: arrayOf()
            val checkedItems = BooleanArray(ingredients.size) // 初期状態ではすべての項目を未選択に設定

            AlertDialog.Builder(requireContext())
                .setTitle("成分を選択")
                .setMultiChoiceItems(ingredients, checkedItems) { _, which, isChecked ->
                    checkedItems[which] = isChecked
                }
                .setPositiveButton("OK") { _, _ ->
                    val selectedIngredients =
                        ingredients.filterIndexed { index, _ -> checkedItems[index] }
                    vm.selectedIngredients.value = selectedIngredients
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun setHideKeyBoard(view: View) {
        // タッチリスナーを設定
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!isKeyboardShown(view)) {
                        requireContext().hideKeyboard(view)
                    }
                }

                MotionEvent.ACTION_UP -> {
                    v.performClick()
                }
            }
            true
        }
    }

    private fun isKeyboardShown(rootView: View): Boolean {
        val softKeyboardHeight = 100
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val screenHeight = rootView.height
        val keypadHeight = screenHeight - r.bottom
        return keypadHeight > softKeyboardHeight
    }
}
