package com.purelab.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val id: String? = null,
    val name: String? = null
) : Parcelable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}

fun mockCategory(): Category = Category(
    id = "0001",
    name = "キュレル"
)

fun Category.toMap(): Map<String, *> {
    return hashMapOf(
        "id" to this.id,
        "name" to this.name,
    )
}