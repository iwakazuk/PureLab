package com.purelab.view.itemlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.purelab.models.Category
import com.purelab.models.Item
import com.purelab.repository.FirestoreRepository

class ItemListViewModel : ViewModel() {
    private val firestoreRepository = FirestoreRepository(FirebaseFirestore.getInstance())

    val category = MutableLiveData<Category>(Category())
    val items = MutableLiveData<List<Item>>()

    fun setCategory(newCategory: Category) {
        category.value = newCategory
    }

    fun fetchItems() {
        firestoreRepository.fetchItemsByCategory(category.value) {
            items.value = it
        }
    }
}