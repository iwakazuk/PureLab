package com.purelab.view.mypage

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.purelab.R
import com.purelab.databinding.FragmentNotification2Binding
import com.purelab.view.BaseDataBindingFragment

class NotificationFragment2 : BaseDataBindingFragment<FragmentNotification2Binding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_notification2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = dataBinding!!

        binding.btnNextPage.setOnClickListener {
            findNavController().navigate(R.id.action_notificationFragment2_to_notificationFragment3)
        }

    }

}
