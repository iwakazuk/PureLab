package com.purelab.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.purelab.models.Item
import com.purelab.models.mockItem
import com.purelab.repository.FirestoreRepository

class HomeViewModel : ViewModel() {
    private val firestoreRepository = FirestoreRepository(FirebaseFirestore.getInstance())

    private val newResult = MutableLiveData<List<Item>>()
    val data: LiveData<List<Item>> = newResult

    fun fetchNewItems() {
        firestoreRepository.fetchNewItems {
            newResult.value = it
        }
    }

    private fun mapDocumentToItemList(document: DocumentSnapshot): List<Item> {
        return listOf(
            mockItem(),
            mockItem(),
            mockItem(),
            mockItem()
        )
    }
}