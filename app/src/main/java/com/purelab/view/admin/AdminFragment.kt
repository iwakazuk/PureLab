package com.purelab.view.admin

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.purelab.R
import com.purelab.databinding.AdminInputSectionBinding
import com.purelab.databinding.FragmentAdminBinding
import com.purelab.utils.CustomSnackbar
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

        // エラーメッセージをバインド
        vm.firestoreRepository.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                // エラーメッセージがnullでない場合にダイアログを表示
                context?.let { it1 ->
                    AlertDialog.Builder(it1)
                        .setMessage(it)
                        .setPositiveButton("OK", null)
                        .show()
                }

                // エラーメッセージをnullにリセットして、同じエラーのダイアログが再表示されるのを防ぐ
                vm.firestoreRepository.errorMessage.value = null
            }
        }

        // 全データのロード
        vm.loadData()

        // キーボードの設定
        settingKeyBord(binding.adminName)

        // 各セクションの描画
        setItemSection()
        setupInputSection(binding.brandInputSection, "ブランド")
        setupInputSection(binding.categoryInputSection, "カテゴリ")
        setupInputSection(binding.ingredientInputSection, "成分")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 各種ボタンの設定
        setIngredientButton()
        setupItemSaveButton()
        setupBrandSaveButton(binding.brandInputSection)
        setupCategorySaveButton(binding.categoryInputSection)
        setupIngredientSaveButton(binding.ingredientInputSection)
    }

    /** 商品を追加セクションの描画設定 */
    private fun setItemSection() {
        // ブランドのピッカーを設定
        vm.brands.observe(viewLifecycleOwner) { brandMap ->
            val brandList = brandMap.map { it.name }
            val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, brandList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.adminBrand.adapter = adapter
        }

        // カテゴリのピッカーを設定
        vm.categories.observe(viewLifecycleOwner) { categoryMap ->
            val categoryList = categoryMap.map { it.name }
            val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, categoryList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.adminCategory.adapter = adapter
        }

        // 成分のピッカーを設定
        vm.selectedIngredients.observe(viewLifecycleOwner) { ingredients ->
            val text = if (ingredients.isEmpty()) {
                "成分を選択"
            } else {
                ingredients.joinToString(separator = "\n")
            }
            binding.adminIngredients.text = text
        }
    }

    /** 各セクションのレイアウトを描画 */
    private fun setupInputSection(
        section: AdminInputSectionBinding,
        label: String
    ) {
        section.titleText.text = label + "を追加"
        section.labelText.text = label + "名"
        section.inputField.hint = label + "名を入力"
        section.saveButton.text = "保存"
        settingKeyBord(section.inputField)
    }

    /** 商品追加セクションの保存ボタンのクリックリスナーを設定 */
    private fun setupItemSaveButton() {
        binding.saveButton.setOnClickListener { v ->
            val name: String = binding.adminName.text.toString()
            val brandingID = binding.adminBrand.textAlignment.toString()
            val categoryID = binding.adminCategory.textAlignment.toString()
            val ingredientIDs = listOf(vm.selectedIngredients.value)

            if (name.isEmpty()) {
                CustomSnackbar.showSnackBar(v, "商品名を入力してください")
                return@setOnClickListener
            } else if (brandingID == null) {
                CustomSnackbar.showSnackBar(v, "ブランド名を入力してください")
                return@setOnClickListener
            } else if (categoryID == null) {
                CustomSnackbar.showSnackBar(v, "カテゴリを選択してください")
                return@setOnClickListener
            } else if (ingredientIDs.isEmpty()) {
                CustomSnackbar.showSnackBar(v, "成分を選択してください")
                return@setOnClickListener
            }

            val product = hashMapOf(
                "name" to name,
                "brandID" to brandingID,
                "categoryID" to categoryID,
                "ingredientIDs" to ingredientIDs
            )
            vm.saveItem(product)

            // 保存完了のsnackbarを表示
            CustomSnackbar.showSnackBar(v, "商品を保存しました")
        }
    }

    /** ブランド追加セクションの保存ボタンのクリックリスナーを設定 */
    private fun setupBrandSaveButton(binding: AdminInputSectionBinding) {
        binding.saveButton.setOnClickListener { v ->
            val name: String = binding.inputField.text.toString()
            if (name.isEmpty()) {
                CustomSnackbar.showSnackBar(v, "ブランド名を入力してください")
                return@setOnClickListener
            }
            val product = hashMapOf("name" to name)
            vm.saveBrand(product)

            // 保存完了のsnackbarを表示
            CustomSnackbar.showSnackBar(v, "ブランド名を保存しました")
        }
    }


    /** カテゴリ追加セクションの保存ボタンのクリックリスナーを設定 */
    private fun setupCategorySaveButton(binding: AdminInputSectionBinding) {
        binding.saveButton.setOnClickListener { v ->
            val name: String = binding.inputField.text.toString()
            if (name.isEmpty()) {
                CustomSnackbar.showSnackBar(v, "カテゴリ名を入力してください")
                return@setOnClickListener
            }
            val product = hashMapOf("name" to name)
            vm.saveCategory(product)

            // 保存完了のsnackbarを表示
            CustomSnackbar.showSnackBar(v, "カテゴリ名を保存しました")
        }
    }


    /** 成分追加セクションの保存ボタンのクリックリスナーを設定 */
    private fun setupIngredientSaveButton(binding: AdminInputSectionBinding) {
        binding.saveButton.setOnClickListener { v ->
            val name: String = binding.inputField.text.toString()
            if (name.isEmpty()) {
                CustomSnackbar.showSnackBar(v, "成分名を入力してください")
                return@setOnClickListener
            }
            val product = hashMapOf("name" to name)
            vm.saveIngredient(product)

            // 保存完了のsnackbarを表示
            CustomSnackbar.showSnackBar(v, "成分名を保存しました")
        }
    }

    /** 成分選択ボタンの設定 */
    private fun setIngredientButton() {
        binding.adminIngredients.setOnClickListener { v ->
            val ingredients = vm.ingredients.value?.map { it.name }?.toTypedArray() ?: arrayOf()
            val checkedItems = BooleanArray(ingredients.size) // 初期状態ではすべての項目を未選択に設定

            AlertDialog.Builder(requireContext())
                .setTitle("成分を選択")
                .setMultiChoiceItems(ingredients, checkedItems) { _, which, isChecked ->
                    checkedItems[which] = isChecked
                }
                .setPositiveButton("OK") { _, _ ->
                    val selectedIngredients =
                        ingredients.filterIndexed { index, _ -> checkedItems[index] }
                    val selectedIngredientsPair = vm.ingredients.value?.mapNotNull {
                        if (selectedIngredients.contains(it.name)) {
                            it
                        } else {
                            null
                        }
                    }

                    vm.selectedIngredients.value = selectedIngredientsPair
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun settingKeyBord(editText: EditText) {
        editText.inputType = InputType.TYPE_CLASS_TEXT // Add this line
        editText.setOnKeyListener { _, keyCode, event ->  // Add this block
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboardFrom(requireContext(), editText)
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboardFrom(requireContext(), binding.root)
    }
}
