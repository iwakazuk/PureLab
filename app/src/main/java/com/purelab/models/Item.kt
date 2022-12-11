package com.purelab.models

import java.io.Serializable

data class Item(
    var id: String? = null,
    val name: String? = null,
    val tagId: String? = null,
    val brandName: String? = null,
    val category: String? = null,
    val detail: String? = null,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}

fun mockItem(): Item = Item(
    id = "0001",
    name = "a",
    tagId = "1",
    brandName = "a",
    category = "a",
    detail = "a"
)

//fun Item.toMap(): Map<String, *> {
//    return hashMapOf(
//        "id" to this.id,
//        "name" to this.name,
//        "tagId" to this.tagId,
//        "brandName" to this.brandName,
//        "category" to this.category,
//        "detail" to this.detail,
//    )
//}
//
//fun Map<String, Any>.toItem(): Item {
//    val id = this["id"] as String
//    val name = this["name"] as String
//    val tagId = this["tagId"] as String
//    val brandName = this["brandName"] as String
//    val category = this["category"] as String
//    val detail = this["detail"] as String
//    return Item(id, name, tagId, brandName, category, detail)
//}
