package com.purelab.app

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationBarView
import com.purelab.R
import com.purelab.databinding.ActivityMainBinding
import com.purelab.models.mockItem
import com.purelab.repository.ItemRepository
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

//    @Inject
//    lateinit var configRepository: ConfigRepository
//
//    @Inject
//    lateinit var appUpdateManager: AppUpdateManager

    private val hideAppBarDestinationIds = setOf(
        R.id.navi_home,
        R.id.navi_search,
        R.id.navi_favorite,
        R.id.navi_mypage
    )

    private val rootNavigationDestinationIds = setOf(
        R.id.navi_home,
        R.id.navi_search,
        R.id.navi_favorite,
        R.id.navi_mypage
    )

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO: スプラッシュ画面
        // installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        binding.appbar.applyInsetter {
            type(
                statusBars = true,
            ) {
                padding(
                    top = true
                )
            }
        }

        val navController = binding.navHostFragment.getFragment<NavHostFragment>().navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.requireNavigationView.setupWithNavController(navController)
        binding.requireNavigationView.setOnItemReselectedListener { }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isHideAppBar = hideAppBarDestinationIds.contains(destination.id)
            binding.appbar.visibility(!isHideAppBar)

            val isRootDestination = rootNavigationDestinationIds.contains(destination.id)
            binding.requireNavigationView.isVisible = isRootDestination
            updateInsets(!isRootDestination)

//            val isDarkStatusBarDestination = darkStatusBarDestinationIds.contains(destination.id)
//            val windowInsetController = WindowInsetsControllerCompat(window, window.decorView)
//            windowInsetController.isAppearanceLightStatusBars =
//                !isDarkStatusBarDestination && !isDarkTheme
        }

        // todo:データ取得用
        val repository = ItemRepository()
        lifecycleScope.launch {
            repository.add(mockItem())
        }
    }

    override fun onResume() {
        super.onResume()
        checkUpdate()
    }

    override fun onSupportNavigateUp() = checkNotNull(
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
    ).findNavController().navigateUp()

    private fun updateInsets(isApplyInsets: Boolean) {
        binding.rootLayout.applyInsetter {
            if (isApplyInsets) {
                type(
                    displayCutout = true,
                    statusBars = true,
                    navigationBars = true,
                    captionBar = true
                ) {
                    padding(horizontal = true)
                }
            } else {
                binding.rootLayout.updatePadding(left = 0, right = 0)
            }
        }
    }

    private fun checkUpdate() {
//        appUpdateManager.requestUpdateFlow()
//            .onEach { appUpdate ->
//                when (appUpdate) {
//                    is AppUpdateResult.Available -> {
//                        binding.requireNavigationView.getOrCreateBadge(R.id.navSettings)
//                    }
//                    else -> {
//                        binding.requireNavigationView.removeBadge(R.id.navSettings)
//                    }
//                }
//            }
//            .catch {}
//            .launchWhenStartedIn(lifecycleScope)
    }

    private fun AppBarLayout.visibility(isVisibility: Boolean) {
        updateLayoutParams<ViewGroup.LayoutParams> {
//            height = if (isVisibility) ViewGroup.LayoutParams.WRAP_CONTENT else 0
        }
    }

//    private val ShapeTheme.themeRes: Int
//        get() {
//            return when (this) {
//                ShapeTheme.ROUNDED -> R.style.Theme_MaterialGallery_DayNight_Rounded
//                ShapeTheme.CUT -> R.style.Theme_MaterialGallery_DayNight_Cut
//            }
//        }

    private val ActivityMainBinding.requireNavigationView: NavigationBarView
        get() = bottomNav as NavigationBarView
}