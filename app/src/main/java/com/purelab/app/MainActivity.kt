package com.purelab.app

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.purelab.R
import com.purelab.databinding.ActivityMainBinding
import com.purelab.models.mockItem
import com.purelab.repository.ItemRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val hideAppBarDestinationIds = setOf(
        R.id.navi_search,
        R.id.item_list_view,
    )

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        findViewById<BottomNavigationView>(R.id.bottom_nav)
            .setupWithNavController(navController)


        // todo:データ取得用
        val repository = ItemRepository()
        lifecycleScope.launch {
            repository.add(mockItem())
        }
    }

    private fun AppBarLayout.visibility(isVisibility: Boolean) {
        updateLayoutParams<ViewGroup.LayoutParams> {
            height = if (isVisibility) ViewGroup.LayoutParams.WRAP_CONTENT else 0
        }
    }
}