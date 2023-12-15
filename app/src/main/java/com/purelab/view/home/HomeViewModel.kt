package com.purelab.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.purelab.models.Item
import com.purelab.repository.FirestoreRepository

class HomeViewModel : ViewModel() {
    private val firestoreRepository = FirestoreRepository(FirebaseFirestore.getInstance())

    val newResult = MutableLiveData<List<Item>>()
    val isLoaded = MutableLiveData<Boolean>()

    fun fetchNewItems() {
        isLoaded.postValue(true)
        firestoreRepository.fetchNewItems {
            newResult.value = it
            isLoaded.postValue(false)
        }
    }
}