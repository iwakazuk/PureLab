package com.purelab.view.itemdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.purelab.R
import com.purelab.databinding.FragmentItemdetailBinding
import com.purelab.utils.CustomSnackbar
import com.purelab.view.BaseDataBindingFragment

class ItemDetailFragment : BaseDataBindingFragment<FragmentItemdetailBinding>() {

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

        setFragmentResultListener("request_key") { _, bundle ->
            val category = bundle.getString("category")
            binding.itemDetailCategory.text = category
        }
        binding.itemDetailBrand.text = "キュレル（Curel）"
        binding.itemDetailTitle.text = "キュレル 化粧水Ⅲ"
        binding.itemDetailText.text =
            "●「セラミド機能カプセル※（保湿）」配合。洗顔後、スキンケア前の乾燥対策をしていない「無防備肌」に、速やかに角層まで潤いを届け、抱え込むように保つ。\n●潤い成分（ユーカリエキス）配合。外部刺激で肌荒れしにくい、なめらかで潤いに満ちた肌に保つ。\n●消炎剤配合。肌荒れを防ぐ。\n●とてもしっとり潤う使い心地。　※カプセル状のセラミド機能成分（ヘキサデシロキシＰＧヒドロキシエチルヘキサデカナミド）【医薬部外品】"

        // LiveDataの変更を監視
        vm.isFavorited.observe(viewLifecycleOwner) { isFavorited ->
            if (isFavorited) {
                binding.itemDetailFavorite.setImageResource(R.drawable.heart_circle_check_solid)
            } else {
                binding.itemDetailFavorite.setImageResource(R.drawable.heart_circle_plus_solid)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemDetailFavorite.setOnClickListener {

            if (vm.isFavorited.value == true) {
                // 登録解除のsnackbarを表示
                CustomSnackbar.showSnackBar(view, "お気に入り登録を解除しました")
            } else {
                // 保存完了のsnackbarを表示
                CustomSnackbar.showSnackBar(view, "お気に入り登録が完了しました")
            }

            vm.toggleFavorite()
        }
    }
}
