package com.purelab.view.itemdetail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.purelab.models.Favorite
import com.purelab.models.Item
import com.purelab.repository.RealmRepository
import io.realm.Realm

class ItemDetailViewModel(
    application: Application,
    private val realmRepository: RealmRepository
) : AndroidViewModel(application) {
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
        val currentItemId = item.value?.id ?: return

        val favorites: List<Favorite>? = realmRepository.getFavorites()
        val matchingFavorite = favorites?.firstOrNull { it.itemId == currentItemId }

        matchingFavorite?.let {
            favoriteLiveData.postValue(it)
            _isFavorited.postValue(true)
        }
    }

    /** 新しいデータを追加 */
    fun saveFavorite() {
        val currentItemId = item.value?.id ?: return
        realmRepository.saveFavorite(Favorite("favorite" ,currentItemId))
    }

    /** データを削除 */
    fun deleteFavorite() {
        val currentItemId = item.value?.id ?: return
        val realm = Realm.getDefaultInstance()

        // トランザクションを開始
        realm.beginTransaction()

        // `itemId`に一致するオブジェクトをクエリで取得
        val favoriteToDelete = realm.where(Favorite::class.java).equalTo("itemId", currentItemId).findFirst()

        // オブジェクトを削除
        favoriteToDelete?.deleteFromRealm()

        // トランザクションをコミット
        realm.commitTransaction()

        // Realmインスタンスを閉じる
        realm.close()
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