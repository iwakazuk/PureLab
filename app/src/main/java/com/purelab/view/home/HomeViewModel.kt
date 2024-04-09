package com.purelab.view.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.purelab.models.Favorite
import com.purelab.models.Item
import com.purelab.repository.FirestoreRepository
import com.purelab.repository.RealmRepository

class HomeViewModel(
    application: Application,
    private val realmRepository: RealmRepository
) : AndroidViewModel(application) {
    private val firestoreRepository = FirestoreRepository(FirebaseFirestore.getInstance())
    private var favoriteResults = MutableLiveData<List<Item>>()
    val data: LiveData<List<Item>> = MutableLiveData<List<Item>>()

    val newResult = MutableLiveData<List<Item>>()
    val isLoaded = MutableLiveData<Boolean>()

    fun fetchNewItems() {
        isLoaded.postValue(true)
        firestoreRepository.fetchNewItems {
            newResult.value = it
            isLoaded.postValue(false)
        }
    }

    fun loadFavorite() {
        val favorites: List<Favorite>? = realmRepository.getFavorites()

        val ids = favorites?.map { it.itemId } ?: return
        firestoreRepository.fetchNewItemsById(ids) {
            favoriteResults.postValue(it)
        }
    }

}