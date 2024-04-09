package com.purelab.app

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.purelab.repository.RealmRepository
import com.purelab.view.home.HomeViewModel
import com.purelab.view.mypage.favorite.FavoriteViewModel
import com.purelab.view.itemdetail.ItemDetailViewModel
import com.purelab.view.mypage.MyPageViewModel
import com.purelab.view.mypage.setting.SettingViewModel

class ViewModelFactory(
    private val application: Application,
    private val realmRepository: RealmRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyPageViewModel(application, realmRepository) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(application, realmRepository) as T
        } else if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingViewModel(application, realmRepository) as T
        } else if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(application, realmRepository) as T
        } else if (modelClass.isAssignableFrom(ItemDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemDetailViewModel(application, realmRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
