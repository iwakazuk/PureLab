package com.purelab.view.mypage.admin

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.purelab.models.Brand
import com.purelab.models.Category
import com.purelab.models.Ingredient
import com.purelab.repository.FirestoreRepository

class AdminViewModel(
    application: Application
) : AndroidViewModel(application) {
    val firestoreRepository = FirestoreRepository(FirebaseFirestore.getInstance())

    // LiveData for brands and categories
    private val itemIds = MutableLiveData<List<String>>()
    val brands = MutableLiveData<List<Brand>>()
    val categories = MutableLiveData<List<Category>>()
    val ingredients = MutableLiveData<List<Ingredient>>()
    val selectedIngredients = MutableLiveData<List<Ingredient>>()

    fun loadData() {
        firestoreRepository.loadItemIds {
            itemIds.value = it
        }

        firestoreRepository.loadBrands {
            brands.value = it
        }

        firestoreRepository.loadCategories {
            categories.value = it
        }

        firestoreRepository.loadIngredients {
            ingredients.value = it
        }
    }

    fun saveItem(item: Map<String, Any>) {
        val itemId = itemIds.value?.last()?.toInt()?.plus(1)?.toString()
        if (itemId == null) {
            Log.w("FirestoreApp", "Error retrieving itemId")
            return
        }
        firestoreRepository.saveData(
            FirestoreRepository.COLLECTION_ITEMS,
            itemId,
            item
        )
    }

//    fun saveItems(item: List<Map<String, Any>>) {
//        var itemId = 10000
//
//        item.forEach {
//            itemId += 1
//            firestoreRepository.saveData(
//                FirestoreRepository.COLLECTION_INGREDIENTS,
//                itemId.toString(),
//                it
//            )
//        }
//    }

    fun saveBrand(brand: Map<String, String>) {
        val brandId = brands.value?.map { it.id }?.last()?.toInt()?.plus(1)?.toString()
        if (brandId == null) {
            Log.w("FirestoreApp", "Error retrieving brandId")
            return
        }
        firestoreRepository.saveData(
            FirestoreRepository.COLLECTION_BRANDS,
            brandId,
            brand
        )
    }

    fun saveCategory(category: Map<String, String>) {
        val categoryId = categories.value?.map { it.id }?.last()?.toInt()?.plus(1)?.toString()
        if (categoryId == null) {
            Log.w("FirestoreApp", "Error retrieving categoryId")
            return
        }
        firestoreRepository.saveData(
            FirestoreRepository.COLLECTION_CATEGORIES,
            categoryId,
            category
        )
    }

    fun saveIngredient(ingredient: Map<String, String>) {
        val ingredientId = ingredients.value?.map { it.id }?.last()?.toInt()?.plus(1)?.toString()
        if (ingredientId == null) {
            Log.w("FirestoreApp", "Error retrieving ingredientId")
            return
        }
        firestoreRepository.saveData(
            FirestoreRepository.COLLECTION_INGREDIENTS,
            ingredientId,
            ingredient
        )
    }
}