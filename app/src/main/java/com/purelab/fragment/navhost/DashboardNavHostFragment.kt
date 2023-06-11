package com.purelab.fragment.navhost

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.purelab.R
import com.purelab.databinding.FragmentNavhostDashboardBinding
import com.purelab.fragment.blankfragment.BaseDataBindingFragment
import com.purelab.util.Event
import com.purelab.viewmodel.NavControllerViewModel


class DashboardNavHostFragment : BaseDataBindingFragment<FragmentNavhostDashboardBinding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_navhost_dashboard

    private val navControllerViewModel by activityViewModels<NavControllerViewModel>()


    private var navController: NavController? = null

    private val nestedNavHostFragmentId = R.id.nestedDashboardNavHostFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nestedNavHostFragment =
            childFragmentManager.findFragmentById(nestedNavHostFragmentId) as? NavHostFragment
        navController = nestedNavHostFragment?.navController

    }

    override fun onResume() {
        super.onResume()

        // Set this navController as ViewModel's navController
        navController?.let {
            navControllerViewModel.currentNavController.value = Event(it)
        }
    }


}