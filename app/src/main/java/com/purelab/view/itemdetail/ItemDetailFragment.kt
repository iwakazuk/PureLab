package com.purelab.view.itemdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.purelab.R
import com.purelab.databinding.FragmentItemdetailBinding
import com.purelab.models.Item
import com.purelab.utils.CustomSnackbar
import com.purelab.view.BaseDataBindingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ItemDetailFragment : BaseDataBindingFragment<FragmentItemdetailBinding>() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override fun getLayoutRes(): Int = R.layout.fragment_itemdetail
    private lateinit var binding: FragmentItemdetailBinding
    private val vm: ItemDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentItemdetailBinding.inflate(inflater, container, false)

        // 遷移前画面からItemに詰めたデータを取得
        val item: Item? = arguments?.getParcelable("item")
        vm.item.value = item

        vm.item.observe(viewLifecycleOwner) { data ->
            binding.itemDetailCategory.text = data.category
            binding.itemDetailBrand.text = data.brandName
            binding.itemDetailName.text = data.name
            binding.itemDetailText.text = data.detail
        }

        // お気に入り登録/解除ボタンの変更を監視
        vm.isFavorited.observe(viewLifecycleOwner) { isFavorited ->
            if (isFavorited) {
                binding.itemDetailFavorite.setImageResource(R.drawable.heart_circle_check_solid)
            } else {
                binding.itemDetailFavorite.setImageResource(R.drawable.heart_circle_plus_solid)
            }
        }

        vm.loadFavorite()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemDetailFavorite.setOnClickListener {

            if (vm.isFavorited.value == true) {
                vm.deleteFavorite()
                // 登録解除のsnackbarを表示
                CustomSnackbar.showSnackBar(view, "お気に入り登録を解除しました")
            } else {
                // データを保存
                vm.saveFavorite()
                // 保存完了のsnackbarを表示
                CustomSnackbar.showSnackBar(view, "お気に入り登録が完了しました")
            }

            vm.toggleFavorite()
        }
    }
}
