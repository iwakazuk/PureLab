package com.purelab.view.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.purelab.models.Item
import com.purelab.models.mockItem

class FavoriteViewModel : ViewModel() {
    private var newResult = MutableLiveData<List<Item>>()
    val data: LiveData<List<Item>> = newResult

    fun fetchData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("items")
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<Item>()
                for (document in result) {
                    val myData = mapDocumentToItemList(document)
                    itemList.add(myData)
                }
                newResult.value = itemList
            }
            .addOnFailureListener { exception ->
                // エラー処理を書きます。
                println(exception)
            }
    }

    fun addData() {
        val db = FirebaseFirestore.getInstance()

        // 作成するデータのオブジェクト
        val data = hashMapOf(
            "item1" to mockItem(),
            "item2" to mockItem()
        )

        db.collection("favorite")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.d("MyApp", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.e("MyApp", "Error adding document", exception)
            }
    }

    fun updateData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("favorite")
            .document("specificId")
            .update("key1", "newValue1", "key2", "newValue2")
            .addOnSuccessListener {
                Log.d("MyApp", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { exception ->
                Log.e("MyApp", "Error updating document", exception)
            }
    }

    private fun mapDocumentToItemList(document: DocumentSnapshot): Item {
        val data = document.data

        val brand = data?.get("brandName") as String?
        val category = data?.get("category") as String?
        val detail = data?.get("detail") as String?
        val id = data?.get("id") as String?
        val name = data?.get("name") as String?

        return Item(
            id = id,
            name = name,
            brand = brand,
            category = category,
            description = detail
        )
    }
}