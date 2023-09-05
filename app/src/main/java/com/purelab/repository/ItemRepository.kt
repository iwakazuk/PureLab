package com.purelab.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.purelab.models.Item
import com.purelab.models.toMap
import com.purelab.utils.Constants
import kotlinx.coroutines.tasks.await

class ItemRepository {
    private val mFireStore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    suspend fun add(items: Item): Boolean {
        return try {
            val collection = mFireStore.collection(Constants.ITEMS)
            val document = collection.document(items.itemId ?: "001")
            val data = items.toMap()
            document.set(data).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getItem(): ArrayList<Item> {
        val productList: ArrayList<Item> = ArrayList()
        mFireStore.collection(Constants.ITEMS)
//            .whereEqualTo(Constants.ITEM_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i("Item List ::", document.documents.toString())

                for (i in document.documents) {
                    val product = i.toObject(Item::class.java)!!
                    product.itemId = i.id
                    productList.add(product)
                }
            }
            .addOnFailureListener { e ->
                Log.e("getItem()", "Error while getting Item list", e)
            }
        return productList
    }

//    suspend fun delete(item: Item): Boolean {
//        return try {
//            val collection = database.collection(Constants.ITEMS)
//            val document = collection.document(item.itemId ?: "001")
//            document.delete().await()
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }
}
