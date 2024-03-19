package com.purelab.view.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.purelab.R
import com.purelab.app.ViewModelFactory
import com.purelab.databinding.FragmentMypageBinding
import com.purelab.repository.RealmRepository
import com.purelab.view.BaseDataBindingFragment

/** マイページ画面 */
class MyPageFragment : BaseDataBindingFragment<FragmentMypageBinding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_mypage
    private lateinit var binding: FragmentMypageBinding

    private val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(
            requireActivity().application,
            RealmRepository()
        )
    }

    private val vm: MyPageViewModel by viewModels { viewModelFactory }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        binding.buttonFavorite.setOnClickListener {
            findNavController().navigate(R.id.action_mypage_to_favorite)
        }

        binding.buttonSetting.setOnClickListener {
            findNavController().navigate(R.id.action_mypage_to_setting)
        }

        binding.buttonAdmin.setOnClickListener {
            findNavController().navigate(R.id.action_mypage_to_admin)
        }

        // ユーザーデータをビューにセットする
        binding.userName.text = vm.loadUser()?.userName ?: "未設定"

        return binding.root
    }
}
