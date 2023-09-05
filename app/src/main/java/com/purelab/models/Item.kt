package com.purelab.models

import java.io.Serializable

data class Item(
    var itemId: String? = null,
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
    itemId = "0001",
    name = "キュレル 化粧水 III",
    tagId = "ああああ",
    brandName = "キュレル（Curel）",
    category = "化粧水",
    detail = "皮膚トラブルケア化粧水です"
)

fun Item.toMap(): Map<String, *> {
    return hashMapOf(
        "itemId" to this.itemId,
        "name" to this.name,
        "tagId" to this.tagId,
        "brandName" to this.brandName,
        "category" to this.category,
        "detail" to this.detail,
    )
}

fun Map<String, Any>.toItem(): Item {
    val itemId = this["itemId"] as String
    val name = this["name"] as String
    val tagId = this["tagId"] as String
    val brandName = this["brandName"] as String
    val category = this["category"] as String
    val detail = this["detail"] as String
    return Item(itemId, name, tagId, brandName, category, detail)
}
