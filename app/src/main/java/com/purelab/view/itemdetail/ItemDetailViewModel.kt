package com.purelab.view.itemdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.purelab.models.Favorite
import com.purelab.models.Item
import io.realm.Realm

class ItemDetailViewModel : ViewModel() {
    /** アイテムID */
    var itemId: String = ""

    /** 商品詳細 */
    val item = MutableLiveData(Item())

    /**
     * お気に入り登録機能
     */
    private val realm: Realm = Realm.getDefaultInstance()
    private val favoriteLiveData: MutableLiveData<Favorite> = MutableLiveData()

    /** 新しいデータを取得 */
    fun loadFavorite() {
        val currentItemId = item.value?.itemId ?: return

        realm.executeTransactionAsync { bgRealm ->
            val favoritesList = bgRealm.where(Favorite::class.java)
                .equalTo("itemId", currentItemId)
                .findAll()
                .let { bgRealm.copyFromRealm(it) }

            val matchingFavorite = favoritesList.firstOrNull { it.itemId == currentItemId }

            matchingFavorite?.let {
                favoriteLiveData.postValue(it)
                _isFavorited.postValue(true)
            }
        }
    }

    /** 新しいデータを追加 */
    fun saveFavorite() {
        val currentItemId = item.value?.itemId ?: return
        realm.executeTransactionAsync ({ bgRealm ->
            val newFavorite = Favorite(currentItemId)
            bgRealm.copyToRealmOrUpdate(newFavorite)
        }, { error ->
            // エラー処理
            Log.e("ItemDetailViewModel", "Realm transaction failed", error)
        })
    }

    /** インスタンスを閉じる */
    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    /**
     * お気に入り登録/解除ボタン
     */
    private val _isFavorited = MutableLiveData(false)
    val isFavorited: LiveData<Boolean> get() = _isFavorited

    fun toggleFavorite() {
        _isFavorited.value = _isFavorited.value?.not()
    }
}