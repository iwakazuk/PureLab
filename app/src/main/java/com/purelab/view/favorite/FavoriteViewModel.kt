package com.purelab.view.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.purelab.models.Favorite
import com.purelab.models.Item
import com.purelab.repository.FirestoreRepository
import com.purelab.repository.RealmRepository

class FavoriteViewModel(
    application: Application,
    private val realmRepository: RealmRepository
) : AndroidViewModel(application) {
    private val firestoreRepository = FirestoreRepository(FirebaseFirestore.getInstance())
    private var favoriteResults = MutableLiveData<List<Item>>()
    val data: LiveData<List<Item>> = MutableLiveData<List<Item>>()

    fun loadFavorite() {
        val favorites: List<Favorite>? = realmRepository.getFavorites()

        val ids = favorites?.map { it.itemId } ?: return
        firestoreRepository.fetchNewItemsById(ids) {
            favoriteResults.postValue(it)
        }
    }

    fun savedFavorite(favorite: Favorite) {
        realmRepository.saveFavorite(favorite)
    }

    override fun onCleared() {
        super.onCleared()
        realmRepository.close()
    }
}