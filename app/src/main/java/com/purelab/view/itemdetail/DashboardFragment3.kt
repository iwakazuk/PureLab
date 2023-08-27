package com.purelab.view.itemdetail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.purelab.R
import com.purelab.databinding.FragmentDashboard3Binding
import com.purelab.view.BaseDataBindingFragment

class DashboardFragment3 : BaseDataBindingFragment<FragmentDashboard3Binding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_dashboard3


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = dataBinding!!

        binding.btnGoToStart.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment3_to_searchFragment)
        }

    }
}