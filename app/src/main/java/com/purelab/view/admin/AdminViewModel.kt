package com.purelab.view.admin

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

class AdminViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance()

    // LiveData for brands and categories
    private val itemIds = MutableLiveData<List<String>>()
    val brands = MutableLiveData<List<Pair<String, String>>>()
    val categories = MutableLiveData<List<Pair<String, String>>>()
    val ingredients = MutableLiveData<List<Pair<String, String>>>()
    val selectedIngredients = MutableLiveData<List<Pair<String, String>>>()

    fun loadItems() {
        db.collection("items").get().addOnSuccessListener { querySnapshot ->
            val itemIdList = querySnapshot.documents.mapNotNull { it.id }
            itemIds.value = itemIdList
        }
    }

    fun loadBrands() {
        db.collection("brands").get().addOnSuccessListener { querySnapshot ->
            val brandList = querySnapshot.documents.mapNotNull {
                val name = it.getString("name")
                if (name != null) Pair(it.id, name) else null
            }
            brands.value = brandList
        }
    }

    fun loadCategories() {
        db.collection("categories").get().addOnSuccessListener { querySnapshot ->
            val categoryList = querySnapshot.documents.mapNotNull {
                val name = it.getString("name")
                if (name != null) Pair(it.id, name) else null
            }
            categories.value = categoryList
        }
    }

    fun loadIngredients() {
        db.collection("ingredients").get().addOnSuccessListener { querySnapshot ->
            val categoryList = querySnapshot.documents.mapNotNull {
                val name = it.getString("name")
                if (name != null) Pair(it.id, name) else null
            }
            categories.value = categoryList
        }
    }

    fun saveItem(item: Map<String, Any>) {
        val itemId = itemIds.value?.last()?.toInt()?.plus(1)?.toString()
        if (itemId == null) {
            Log.w("FirestoreApp", "Error retrieving itemId")
            return
        }

        db.collection("items")
            .document(itemId)
            .set(item)
            .addOnSuccessListener {
                Log.d("FirestoreApp", "DocumentSnapshot added with ID: $itemId")
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreApp", "Error adding document", e)
            }
    }

    fun saveBrand(brand: Map<String, String>) {
        val brandId = brands.value?.map { it.first }?.last()?.toInt()?.plus(1)?.toString()
        if (brandId == null) {
            Log.w("FirestoreApp", "Error retrieving brandId")
            return
        }

        db.collection("brands")
            .document(brandId)
            .set(brand)
            .addOnSuccessListener {
                Log.d("FirestoreApp", "DocumentSnapshot added with ID: $brandId")
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreApp", "Error adding document", e)
            }
    }

    fun saveCategory(category: Map<String, String>) {
        val categoryId = categories.value?.map { it.first }?.last()?.toInt()?.plus(1)?.toString()
        if (categoryId == null) {
            Log.w("FirestoreApp", "Error retrieving categoryId")
            return
        }

        db.collection("brands")
            .document(categoryId)
            .set(category)
            .addOnSuccessListener {
                Log.d("FirestoreApp", "DocumentSnapshot added with ID: $categoryId")
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreApp", "Error adding document", e)
            }
    }

    fun saveIngredient(ingredient: Map<String, String>) {
        val ingredientId = ingredients.value?.map { it.first }?.last()?.toInt()?.plus(1)?.toString()
        if (ingredientId == null) {
            Log.w("FirestoreApp", "Error retrieving ingredientId")
            return
        }

        db.collection("ingredients")
            .document(ingredientId)
            .set(ingredient)
            .addOnSuccessListener {
                Log.d("FirestoreApp", "DocumentSnapshot added with ID: $ingredientId")
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreApp", "Error adding document", e)
            }
    }
}