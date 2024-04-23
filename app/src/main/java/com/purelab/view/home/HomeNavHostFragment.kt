package com.purelab.view.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.purelab.R
import com.purelab.databinding.FragmentNavhostHomeBinding
import com.purelab.view.BaseDataBindingFragment
import com.purelab.util.Event
import com.purelab.view.NavControllerViewModel


class HomeNavHostFragment : BaseDataBindingFragment<FragmentNavhostHomeBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_navhost_home

    private val navControllerViewModel by activityViewModels<NavControllerViewModel>()

    private var navController: NavController? = null

    private val nestedNavHostFragmentId = R.id.nestedHomeNavHostFragment

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