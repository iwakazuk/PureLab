package com.purelab.view.itemdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.purelab.R
import com.purelab.app.ViewModelFactory
import com.purelab.databinding.FragmentItemdetailBinding
import com.purelab.models.Item
import com.purelab.repository.RealmRepository
import com.purelab.utils.CustomSnackbar
import com.purelab.view.BaseDataBindingFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/** 商品詳細画面 */
class ItemDetailFragment : BaseDataBindingFragment<FragmentItemdetailBinding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_itemdetail
    private lateinit var binding: FragmentItemdetailBinding
    private val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(
            requireActivity().application,
            RealmRepository()
        )
    }

    private val vm: ItemDetailViewModel by viewModels { viewModelFactory }

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
            binding.itemDetailBrand.text = data.brand
            binding.itemDetailName.text = data.name
            binding.itemDetailText.text = data.description
            // 成分リストをループしてTextViewを追加
            data.ingredients?.forEach { ingredient ->
                val textView = TextView(context).apply {
                    text = "・" + ingredient
                    textSize = 15f
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                binding.itemDetailIngredientsContainer.addView(textView)
            }

            val imageView: ImageView = binding.itemDetailIcon
            val imageUrl = data.image

            imageView.setImageResource(R.drawable.loading_image)

            Picasso.get()
                .load(imageUrl)
                .error(R.drawable.home_image) // エラー時のデフォルト画像
                .into(imageView, object : Callback {
                    override fun onSuccess() {}
                    override fun onError(e: Exception?) {
                        Log.e("PicassoError", "Failed to load image: " + data.image, e)
                    }
                })
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

