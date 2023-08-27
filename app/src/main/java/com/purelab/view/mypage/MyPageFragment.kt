package com.purelab.view.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.purelab.R
import com.purelab.databinding.FragmentMypageBinding
import com.purelab.view.BaseDataBindingFragment

class MyPageFragment : BaseDataBindingFragment<FragmentMypageBinding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_mypage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = dataBinding!!

        binding.buttonFavorite.setOnClickListener {
            findNavController().navigate(R.id.action_mypage_to_favorite)
        }

        binding.buttonSetting.setOnClickListener {
            findNavController().navigate(R.id.action_mypage_to_setting)
        }

        return binding.root
    }
}
