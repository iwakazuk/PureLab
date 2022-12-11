package com.purelab.repository

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.purelab.app.MainActivity
import com.purelab.models.*
import kotlinx.coroutines.tasks.await
import java.util.*

class ItemRepository {
    private val database: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    suspend fun add(items: Items): Boolean {
        return try {
            val collection = database.collection(COLLECTION_PATH)
            val document = collection.document(items.id ?: "001")
            val data = items.toMap()
            document.set(data).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun delete(item: Item): Boolean {
        return try {
            val collection = database.collection(COLLECTION_PATH)
            val document = collection.document(item.id ?: "001")
            document.delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun fetchTask(limit: Long): List<Items> {
        return try {
            val collection: CollectionReference = database.collection(COLLECTION_PATH)
            val query = collection.limit(limit)
            val documents = query.get().await().documents
            val dataList = documents.map { it.data }
            dataList.mapNotNull { it?.toItems() }
        } catch (e: Exception) {
            listOf()
        }
    }

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    fun addTask() {
        database.collection("COLLECTION_PATH")
            .document()
            .set(mockItem())
            .addOnSuccessListener {
                println("送信完了")
            }
            .addOnFailureListener {
                println("送信失敗")
            }
    }

    companion object {
        private const val COLLECTION_PATH = "/items"
    }
}

data class ScoreItem(val name: String = "",
                     val score: Long = 0,
                     val missCount: Int = 0,
                     val time: Long = 0,
                     val registerTime: Date = Date()
)
