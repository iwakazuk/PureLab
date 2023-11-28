package com.purelab.models

data class Category(
    val id: String? = null,
    val name: String? = null
)

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