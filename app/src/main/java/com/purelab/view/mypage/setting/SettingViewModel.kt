package com.purelab.view.mypage.setting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.purelab.models.User
import com.purelab.repository.RealmRepository
import io.realm.Realm

class SettingViewModel(
    application: Application,
    private val realmRepository: RealmRepository
) : AndroidViewModel(application) {
    val userLiveData: MutableLiveData<User> = MutableLiveData()

    fun loadUser() {
        val user = realmRepository.getUser()
        userLiveData.postValue(user)
    }

    fun saveUser(user: User) {
        realmRepository.saveUser(user)
    }

    override fun onCleared() {
        super.onCleared()
        realmRepository.close()
    }
}