package com.purelab.view.setting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.purelab.models.User
import io.realm.Realm

class SettingViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val realm: Realm = Realm.getDefaultInstance()

    val userLiveData: MutableLiveData<User> = MutableLiveData()

    fun loadUser(userId: String) {
        val user = realm.where(User::class.java).equalTo("userId", userId).findFirst()
        userLiveData.postValue(user)
    }

    fun saveUser(user: User) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync { bgRealm ->
            // 既存のデータを全て削除
            bgRealm.where(User::class.java).findAll().deleteAllFromRealm()

            // 新しいデータを追加（または上書き）
            bgRealm.copyToRealmOrUpdate(user)
        }
        realm.close()
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}