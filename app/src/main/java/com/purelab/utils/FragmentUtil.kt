package com.purelab.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.purelab.view.itemlist.ItemListFragment

object FragmentUtils {
    fun showFragment(fragment: Fragment, fragmentManager: FragmentManager, @IdRes container: Int) {
        fragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(container, fragment)
            .commit()
    }
}