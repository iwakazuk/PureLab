package com.purelab.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.purelab.R
import com.purelab.models.Item
import com.purelab.models.mockItem
import com.purelab.repository.ItemRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_nav)
            .setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()

        val repository = ItemRepository()
        lifecycleScope.launch {
//            repository.add(mockItem())
            repository.getItem()
            // ①現在のタスク一覧を取得する
            println("①現在のタスク一覧を取得する ▶ " )
        }
    }
}