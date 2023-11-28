package com.purelab.models

data class Brand(
    val id: String? = null,
    val name: String? = null
)

fun mockBrand(): Brand = Brand(
    id = "0001",
    name = "キュレル"
)

fun Brand.toMap(): Map<String, *> {
    return hashMapOf(
        "id" to this.id,
        "name" to this.name,
    )
}
