package com.purelab.view.mypage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.purelab.R
import com.purelab.databinding.FragmentNavhostMypageBinding
import com.purelab.view.BaseDataBindingFragment
import com.purelab.util.Event
import com.purelab.viewmodel.NavControllerViewModel


class MyPageNavHostFragment : BaseDataBindingFragment<FragmentNavhostMypageBinding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_navhost_mypage

    private val navControllerViewModel by activityViewModels<NavControllerViewModel>()

    private var navController: NavController? = null

    private val nestedNavHostFragmentId = R.id.nestedNotificationNavHostFragment

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