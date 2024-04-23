package com.purelab.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commitNow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter.FragmentTransactionCallback.OnPostEventListener
import com.purelab.view.search.SearchNavHostFragment
import com.purelab.view.home.HomeNavHostFragment
import com.purelab.view.mypage.MyPageNavHostFragment
import com.purelab.view.NavControllerViewModel


/**
 * This [FragmentStateAdapter] is used by Activity and maps [NavController] in [LiveData]
 * to position to be used for setting main Appbar nav graph when ViewPager position changes
 */
class ActivityFragmentStateAdapter(
    fragmentActivity: FragmentActivity,
    private val navControllerViewModel: NavControllerViewModel
) :
    FragmentStateAdapter(fragmentActivity) {

    init {
        // Add a FragmentTransactionCallback to handle changing
        // the primary navigation fragment
        registerFragmentTransactionCallback(object : FragmentTransactionCallback() {
            override fun onFragmentMaxLifecyclePreUpdated(
                fragment: Fragment,
                maxLifecycleState: Lifecycle.State
            ) = if (maxLifecycleState == Lifecycle.State.RESUMED) {


                // This fragment is becoming the active Fragment - set it to
                // the primary navigation fragment in the OnPostEventListener
                OnPostEventListener {
                    fragment.parentFragmentManager.commitNow {
                        setPrimaryNavigationFragment(fragment)
                    }
                }

            } else {
                super.onFragmentMaxLifecyclePreUpdated(fragment, maxLifecycleState)
            }
        })
    }

    override fun getItemCount(): Int = 3

    // 🔥🔥 TODO This does not work with Dynamic Module Navigation
//    override fun createFragment(position: Int): Fragment {
//
//        return when (position) {
//            0 -> NavHostFragment.create(R.navigation.nav_graph_home)
//            1 -> NavHostFragment.create(R.navigation.nav_graph_dashboard)
//            else -> NavHostFragment.create(R.navigation.nav_graph_notification)
//        }
//    }

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> HomeNavHostFragment()
            1 -> SearchNavHostFragment()
            else -> MyPageNavHostFragment()
        }
    }
}