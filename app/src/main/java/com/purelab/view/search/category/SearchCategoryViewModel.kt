package com.purelab.view.search.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.purelab.models.Category
import com.purelab.repository.FirestoreRepository

class SearchCategoryViewModel : ViewModel() {
    private val firestoreRepository = FirestoreRepository(FirebaseFirestore.getInstance())

    val categories = MutableLiveData<List<Category>>()

    fun fetchCategories() {
        firestoreRepository.loadCategories {
            categories.value = it
        }
    }
}
