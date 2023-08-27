package com.purelab.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.purelab.models.Item
import com.purelab.models.mockItem

class HomeViewModel : ViewModel() {
    private val newResult = MutableLiveData<List<Item>>()
    val data: LiveData<List<Item> > = newResult

    fun fetchData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("items")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val myData = mapDocumentToItemList(document)
                    newResult.value = myData
                }
            }
            .addOnFailureListener { exception ->
                // エラー処理を書きます。
                println(exception)
            }
    }

    private fun mapDocumentToItemList(document: DocumentSnapshot): List<Item> {
        return listOf(
            mockItem(),
            mockItem(),
            mockItem(),
            mockItem()
        )
    }
}