package com.purelab.app

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
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
        R.id.navi_home,
        R.id.navi_search,
        R.id.navi_favorite,
        R.id.navi_mypage
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 下タブ
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_nav).setupWithNavController(navController)

        // ツールバー
        val toolbar: androidx.appcompat.widget.Toolbar? = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // アップバー
        val appbar: AppBarLayout = findViewById(R.id.appbar)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isHideAppBar = hideAppBarDestinationIds.contains(destination.id)
            appbar.visibility(!isHideAppBar)
        }

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