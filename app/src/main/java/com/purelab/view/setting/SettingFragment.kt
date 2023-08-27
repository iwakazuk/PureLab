package com.purelab.view.setting

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.purelab.R
import com.purelab.databinding.FragmentSettingBinding
import com.purelab.view.BaseDataBindingFragment

class SettingFragment : BaseDataBindingFragment<FragmentSettingBinding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_setting

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = dataBinding!!

        binding.btnGoToStart.setOnClickListener {
            findNavController().navigate(R.id.action_setting_to_mypage)
        }
    }
}
