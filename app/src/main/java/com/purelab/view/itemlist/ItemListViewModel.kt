package com.purelab.view.itemlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.purelab.models.Category
import com.purelab.models.Ingredient
import com.purelab.models.Item
import com.purelab.repository.FirestoreRepository

class ItemListViewModel : ViewModel() {
    private val firestoreRepository = FirestoreRepository(FirebaseFirestore.getInstance())

    val category = MutableLiveData<Category>(Category())
    val ingredient = MutableLiveData<Ingredient>(Ingredient())
    val items = MutableLiveData<List<Item>>()
    var isCategory = true

    fun setCategory(newCategory: Category) {
        category.value = newCategory
    }

    fun setIngredient(newIngredient: Ingredient) {
        ingredient.value = newIngredient
    }

    fun fetchItemsByCategory() {
        firestoreRepository.fetchItemsByCategory(category.value) {
            items.value = it
        }
    }

    fun fetchItemsByIngredient() {
        firestoreRepository.fetchItemsByIngredient(ingredient.value) {
            items.value = it
        }
    }
}