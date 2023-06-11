package com.purelab.fragment.blankfragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.purelab.R
import com.purelab.databinding.FragmentNotification3Binding
import com.purelab.fragment.blankfragment.BaseDataBindingFragment

class NotificationFragment3 : BaseDataBindingFragment<FragmentNotification3Binding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_notification3

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = dataBinding!!

        binding.btnGoToStart.setOnClickListener {
            findNavController().navigate(R.id.action_notificationFragment3_to_notificationFragment1)
        }
    }
}
