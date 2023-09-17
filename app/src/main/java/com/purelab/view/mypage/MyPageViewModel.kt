package com.purelab.view.mypage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.purelab.models.User
import io.realm.Realm

class MyPageViewModel(
    application: Application
) : AndroidViewModel(application) {

    fun loadUser(userId: String): User? {
        val realm = Realm.getDefaultInstance()
        return realm.where(User::class.java).equalTo("userId", userId).findFirst()
    }
}