package com.purelab.models

import java.io.Serializable

data class Item(
    var item_id: String? = null,
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
    item_id = "0001",
    name = "ああああ",
    tagId = "ああああ",
    brandName = "キュレル（Curel）",
    category = "化粧水",
    detail = "皮膚トラブルケア化粧水"
)

fun Item.toMap(): Map<String, *> {
    return hashMapOf(
        "item_id" to this.item_id,
        "name" to this.name,
        "tagId" to this.tagId,
        "brandName" to this.brandName,
        "category" to this.category,
        "detail" to this.detail,
    )
}

fun Map<String, Any>.toItem(): Item {
    val item_id = this["item_id"] as String
    val name = this["name"] as String
    val tagId = this["tagId"] as String
    val brandName = this["brandName"] as String
    val category = this["category"] as String
    val detail = this["detail"] as String
    return Item(item_id, name, tagId, brandName, category, detail)
}
