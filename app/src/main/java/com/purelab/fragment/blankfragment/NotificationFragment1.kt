package com.purelab.fragment.blankfragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.purelab.R
import com.purelab.databinding.FragmentNotification1Binding

class NotificationFragment1 : BaseDataBindingFragment<FragmentNotification1Binding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_notification1

    private var count = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = dataBinding!!

        binding.tvCount.text = "Count: $count"

        binding.btnIncrease.setOnClickListener {
            binding.tvCount.text = "Count: ${++count}"
        }

        binding.btnNextPage.setOnClickListener {
            findNavController().navigate(R.id.action_notificationFragment1_to_notificationFragment2)
        }

        binding.btnFavorites.setOnClickListener {
            val bundle = bundleOf("count" to count)

            findNavController().navigate(
                R.id.action_notificationFragment1_to_nav_graph_favorites,
                bundle
            )
        }

    }

}
