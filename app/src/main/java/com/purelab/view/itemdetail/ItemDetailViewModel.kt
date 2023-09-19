package com.purelab.view.itemdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.purelab.models.Favorite
import com.purelab.models.Item
import com.purelab.models.User
import io.realm.Realm

class ItemDetailViewModel : ViewModel() {

    private val item = MutableLiveData(Item)

    /**
     * お気に入り登録/解除ボタン
     */
    private val _isFavorited = MutableLiveData(false)
    val isFavorited: LiveData<Boolean> get() = _isFavorited

    fun toggleFavorite() {
        _isFavorited.value = _isFavorited.value?.not()
    }
}