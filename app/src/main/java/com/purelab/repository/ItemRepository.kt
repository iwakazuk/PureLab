package com.purelab.repository

import android.app.Activity
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.purelab.app.MainActivity
import com.purelab.models.*
import com.purelab.utils.Constants
import kotlinx.coroutines.tasks.await
import java.util.*

class ItemRepository {
    private val mFireStore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    suspend fun add(items: Item): Boolean {
        return try {
            val collection = mFireStore.collection(Constants.ITEMS)
            val document = collection.document(items.item_id ?: "001")
            val data = items.toMap()
            document.set(data).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getItem() {
        mFireStore.collection(Constants.ITEMS)
//            .whereEqualTo(Constants.ITEM_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val a = document.documents.toString()
                Log.i("Item List ::", document.documents.toString())
                val productList: ArrayList<Item> = ArrayList()

                for (i in document.documents) {
                    val product = i.toObject(Item::class.java)!!
                    product.item_id = i.id
                    productList.add(product)
                }
                Log.d("TAG", "成功")
//                when (fragment) {
//                    is ItemFragment -> fragment.successProductListFromFirestore(productList)
//                }
            }
            .addOnFailureListener { e ->
                Log.d("TAG", "失敗")
//                when (fragment) {
//                    is ProductsFragment -> fragment.hideProgressDialog()
//                }
//                Log.e(fragment.javaClass.simpleName, "Error while getting Product list", e)
            }
    }


//    suspend fun delete(item: Item): Boolean {
//        return try {
//            val collection = database.collection(Constants.ITEMS)
//            val document = collection.document(item.item_id ?: "001")
//            document.delete().await()
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }
}
