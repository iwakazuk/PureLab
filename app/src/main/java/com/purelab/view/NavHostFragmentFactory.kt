package com.purelab.view

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

class NavHostFragmentFactory private constructor() : FragmentFactory() {

    @LayoutRes
    private var layoutRes: Int = -1

    @IdRes
    private var navHostFragmentId: Int = -1

    fun createNavHostFragment(
        classLoader: ClassLoader,
        className: String,
        @LayoutRes layoutRes: Int,
        @IdRes navHostFragmentId: Int
    ): Fragment {

        this.layoutRes = layoutRes
        this.navHostFragmentId = navHostFragmentId

        println("🔥🔥 NavHostFragment creteNavHostFragment() layoutRes: ${this.layoutRes}, navHostFragmentId: ${this.navHostFragmentId}")

        return instantiate(classLoader, className)
    }


    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {

        println("⚠️😱 NavHostFragment instantiate() layoutRes: ${this.layoutRes}, navHostFragmentId: ${this.navHostFragmentId}")

        return when (className) {
            NavHostContainerFragment::class.java.name -> NavHostContainerFragment(
                layoutRes,
                navHostFragmentId
            )
            else -> super.instantiate(classLoader, className)
        }
    }


    companion object {

        private val navHostFragmentFactory: NavHostFragmentFactory by lazy {
            NavHostFragmentFactory()
        }

        const val KEY_RES_ID = "key-res-id"
        const val KEY_NAV_HOST_ID = "key-nav-host-id"

        @JvmStatic
        fun create(): NavHostFragmentFactory {
            return navHostFragmentFactory
        }

    }

}