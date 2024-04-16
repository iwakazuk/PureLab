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

    /** お気に入りデータ */
    private var favoriteList: List<Favorite>? = null
    private val favoriteLiveData: MutableLiveData<Favorite> = MutableLiveData()

    /** 新しいデータを取得 */
    fun loadFavorite() {
        val itemId = item.value?.id ?: return

        val favorites: List<Favorite>? = realmRepository.getFavorites()
        favoriteList = favorites

        val matchingFavorite = favorites?.firstOrNull { it.itemId == itemId }

        matchingFavorite?.let {
            favoriteLiveData.postValue(it)
            _isFavorited.postValue(true)
        }
    }

    /** 新しいデータを追加 */
    fun saveFavorite() {
        val itemId = item.value?.id ?: return
        val favoriteId = (favoriteList?.count() ?: "1").toString()
        realmRepository.saveFavorite(Favorite(favoriteId ,itemId))
    }

    /** データを削除 */
    fun deleteFavorite() {
        val favoriteId = favoriteLiveData.value?.favoriteId ?: return
        realmRepository.deleteFavorite(favoriteId)
    }

    /** インスタンスを閉じる */
    override fun onCleared() {
        super.onCleared()
        realmRepository.close()
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