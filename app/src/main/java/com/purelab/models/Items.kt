package com.purelab.models

import java.util.*

data class Items(
    val id: String,
    val item: Any
) {
//    companion object {
//        private const val serialVersionUID: Long = 1L
//    }

    companion object {
        fun create(name: String): Items {
            val item= Item()
            return Items(UUID.randomUUID().toString(), item)
        }
    }
}

fun Items.toMap(): Map<String, *> {
    return hashMapOf(
        "id" to this.id,
        "item" to this.item
    )
}

fun Map<String, Any>.toItems(): Items {
    val id = this["id"] as String
    val item = this["items"] as Item
    return Items(id, item)
}
