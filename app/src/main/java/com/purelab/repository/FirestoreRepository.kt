package com.purelab.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

// Firestoreのリポジトリを定義します。
class FirestoreRepository(private val db: FirebaseFirestore) {
    // エラーメッセージを保持するLiveData
    val errorMessage = MutableLiveData<String?>()

    companion object {
        const val COLLECTION_ITEMS = "items"
        const val COLLECTION_BRANDS = "brands"
        const val COLLECTION_CATEGORIES = "categories"
        const val COLLECTION_INGREDIENTS = "ingredients"
    }

    fun loadItemIds(onSuccess: (List<String>) -> Unit) {
        loadData(
            collectionPath = COLLECTION_ITEMS,
            onSuccess = { querySnapshot ->
                onSuccess(querySnapshot.documents.mapNotNull { it.id })
            }
        )
    }

    fun loadBrands(onSuccess: (List<Pair<String, String>>) -> Unit) {
        loadData(
            collectionPath = COLLECTION_BRANDS,
            onSuccess = { querySnapshot ->
                onSuccess(querySnapshot.documents.mapNotNull {
                    val name = it.getString("name")
                    if (name != null) Pair(it.id, name) else null
                })
            }
        )
    }

    fun loadCategories(onSuccess: (List<Pair<String, String>>) -> Unit) {
        loadData(
            collectionPath = COLLECTION_CATEGORIES,
            onSuccess = { querySnapshot ->
                onSuccess(querySnapshot.documents.mapNotNull {
                    val name = it.getString("name")
                    if (name != null) Pair(it.id, name) else null
                })
            }
        )
    }

    fun loadIngredients(onSuccess: (List<Pair<String, String>>) -> Unit) {
        loadData(
            collectionPath = COLLECTION_INGREDIENTS,
            onSuccess = { querySnapshot ->
                onSuccess(querySnapshot.documents.mapNotNull {
                    val name = it.getString("name")
                    if (name != null) Pair(it.id, name) else null
                })
            }
        )
    }

    // 成功時のコールバックと失敗時のコールバックを引数に取る
    private inline fun loadData(
        collectionPath: String,
        crossinline onSuccess: (QuerySnapshot) -> Unit
    ) {
        db.collection(collectionPath).get()
            .addOnSuccessListener { querySnapshot ->
                onSuccess(querySnapshot)
            }
            .addOnFailureListener { exception ->
                errorMessage.value = exception.localizedMessage
            }
    }

    // 他にもデータを保存するための共通関数を追加できます。
    fun saveData(
        collectionPath: String,
        documentId: String,
        data: Any
    ) {
        db.collection(collectionPath).document(documentId)
            .set(data)
            .addOnSuccessListener { Log.d("FirestoreApp", "DocumentSnapshot added with ID: $documentId") }
            .addOnFailureListener { exception ->
                errorMessage.value = exception.localizedMessage
            }
    }
}