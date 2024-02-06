package com.purelab.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
    val id: String? = null,
    val name: String? = null,
    val brand: String? = null,
    val category: String? = null,
    val description: String? = null,
    val ingredients: List<String?>? = null,
) : Parcelable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}

fun mockItem(): Item = Item(
    id = "0001",
    name = "キュレル 化粧水 III",
    brand = "キュレル（Curel）",
    category = "化粧水",
    description = "皮膚トラブルケア化粧水です",
    ingredients = listOf("ヒト型セラミド", "ナイアシンアミド")
)

fun Item.toMap(): Map<String, *> {
    return hashMapOf(
        "name" to this.name,
        "brandName" to this.brand,
        "category" to this.category,
        "description" to this.description,
        "ingredients" to this.ingredients,
    )
}
