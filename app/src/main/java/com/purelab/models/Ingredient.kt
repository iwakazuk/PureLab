package com.purelab.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Ingredient(
    val id: String? = null,
    val name: String? = null
) : Parcelable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}

fun mockIngredient(): Ingredient = Ingredient(
    id = "0001",
    name = "キュレル"
)

fun Ingredient.toMap(): Map<String, *> {
    return hashMapOf(
        "id" to this.id,
        "name" to this.name,
    )
}
