package com.purelab.models

data class Ingredient(
    val id: String? = null,
    val name: String? = null
)

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
