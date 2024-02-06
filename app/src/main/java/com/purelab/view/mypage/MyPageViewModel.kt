package com.purelab.view.mypage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.purelab.models.User
import com.purelab.repository.RealmRepository

class MyPageViewModel(
    application: Application,
    private val realmRepository: RealmRepository
) : AndroidViewModel(application) {
    fun loadUser(): User? {
        return realmRepository.getUser()
    }

    override fun onCleared() {
        super.onCleared()
        realmRepository.close()
    }
}