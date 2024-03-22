package com.purelab.view.search.ingredient

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.purelab.models.Ingredient
import com.purelab.models.Item
import com.purelab.repository.FirestoreRepository

class SearchIngredientViewModel  : ViewModel() {
    private val firestoreRepository = FirestoreRepository(FirebaseFirestore.getInstance())

    val ingredients = MutableLiveData<List<Ingredient>>()

    fun loadIngredients() {
        firestoreRepository.loadIngredients {
            ingredients.value = it
        }
    }
}