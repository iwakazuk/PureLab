package com.purelab.view.setting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.purelab.models.User
import io.realm.Realm

class SettingViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val realm: Realm = Realm.getDefaultInstance()

    fun saveUser(user: User) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { realm ->
            // 既存のデータを全て削除
            realm.where(User::class.java).findAll().deleteAllFromRealm()

            // 新しいデータを追加（または上書き）
            realm.copyToRealmOrUpdate(user)
        }
        realm.close()
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}