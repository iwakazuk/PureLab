package com.purelab.view.admin

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.purelab.models.User
import io.realm.Realm

class AdminViewModel(
    application: Application
) : AndroidViewModel(application) {
    val db = FirebaseFirestore.getInstance()

    // LiveData for brands and categories
    val brands = MutableLiveData<List<String>>()
    val categories = MutableLiveData<List<String>>()
    val ingredients = MutableLiveData<List<String>>()
    val selectedIngredients = MutableLiveData<List<String>>()

    fun loadBrands() {
        db.collection("brands").get().addOnSuccessListener { querySnapshot ->
            val brandList = querySnapshot.documents.mapNotNull { it.getString("name") }
            brands.value = brandList
        }
    }

    fun loadCategories() {
        db.collection("categories").get().addOnSuccessListener { querySnapshot ->
            val categoryList = querySnapshot.documents.mapNotNull { it.getString("name") }
            categories.value = categoryList
        }
    }

    fun loadIngredients() {
        db.collection("ingredients").get().addOnSuccessListener { querySnapshot ->
            val categoryList = querySnapshot.documents.mapNotNull { it.getString("name") }
            categories.value = categoryList
        }
    }

    fun saveItem(item: Map<String, Any>) {
        db.collection("items")
            .add(item)
            .addOnSuccessListener { documentReference ->
                Log.d("FirestoreApp", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreApp", "Error adding document", e)
            }
    }
}