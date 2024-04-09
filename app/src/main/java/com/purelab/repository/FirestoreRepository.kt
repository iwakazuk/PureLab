package com.purelab.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.purelab.models.Brand
import com.purelab.models.Category
import com.purelab.models.Ingredient
import com.purelab.models.Item

// Firestoreのリポジトリを定義します。
class FirestoreRepository(private val db: FirebaseFirestore) {
    // エラーメッセージを保持するLiveData
    val errorMessage = MutableLiveData<String?>()

    companion object {
        const val COLLECTION_ITEMS = "items"
        const val COLLECTION_BRANDS = "brands"
        const val COLLECTION_CATEGORIES = "categories"
        const val COLLECTION_INGREDIENTS = "ingredients"
        const val COLLECTION_NEW = "newItems"
    }

    fun loadItemIds(result: (List<String>) -> Unit) {
        loadData(
            collectionPath = COLLECTION_ITEMS,
            onSuccess = { querySnapshot ->
                result(querySnapshot.documents.mapNotNull { it.id })
            }
        )
    }

    fun fetchItemsByIds(ids: List<String>, result: (List<Item>?) -> Unit) {
        val itemsCollection = db.collection(COLLECTION_ITEMS)
        itemsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val items = mutableListOf<Item>()
                val documents = querySnapshot.documents.filter {
                    return@filter ids.contains(it.id)
                }

                documents.forEach { document ->
                    if (!ids.contains(document.id)) {
                        return@forEach
                    }
                    val itemId = document.id
                    val itemName = document.getString("name") ?: ""
                    val brandId = document.getString("brandId") ?: ""
                    val categoryId = document.getString("categoryId") ?: ""
                    val ingredientIds =
                        document.get("ingredientIds") as? List<String> ?: emptyList()

                    loadBrandName(brandId) { brandName ->
                        loadCategoryName(categoryId) { categoryName ->
                            loadIngredientNames(ingredientIds) { ingredientNames ->
                                val item = Item(
                                    id = itemId,
                                    name = itemName,
                                    brand = brandName,
                                    category = categoryName,
                                    ingredients = ingredientNames
                                )
                                items.add(item)

                                if (items.size == documents.size) {
                                    result(items)
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                errorMessage.value = exception.localizedMessage
                result(null)
            }
    }

    fun fetchItemsByCategory(category: Category?, result: (List<Item>?) -> Unit) {
        val itemsCollection = db.collection(COLLECTION_ITEMS)
        itemsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val items = mutableListOf<Item>()
                val documents = querySnapshot.documents.filter {
                    val categoryId = it.getString("categoryId") ?: ""
                    return@filter category?.id == categoryId
                }

                documents.forEach { document ->
                    val itemId = document.id
                    val itemName = document.getString("name") ?: ""
                    val brandId = document.getString("brandId") ?: ""
                    val ingredientIds =
                        document.get("ingredientIds") as? List<String> ?: emptyList()

                    loadBrandName(brandId) { brandName ->
                        loadIngredientNames(ingredientIds) { ingredientNames ->
                            val item = Item(
                                id = itemId,
                                name = itemName,
                                brand = brandName,
                                category = category?.name,
                                description = document.getString("description")?.replace("\"", "")
                                    ?: "",
                                ingredients = ingredientNames,
                                image = document.getString("imageURL") ?: ""
                            )
                            items.add(item)

                            if (items.size == documents.size) {
                                result(items)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                errorMessage.value = exception.localizedMessage
                result(null)
            }
    }

    fun fetchItemsByIngredient(ingredient: Ingredient?, result: (List<Item>?) -> Unit) {
        val itemsCollection = db.collection(COLLECTION_ITEMS)
        itemsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val items = mutableListOf<Item>()
                val ingredientId = ingredient?.id ?: return@addOnSuccessListener
                val documents = querySnapshot.documents.filter {
                    val ingredientIds =
                        it.get("ingredientIds") as? List<String> ?: return@filter false
                    return@filter ingredientIds.contains(ingredientId)
                }

                documents.forEach { document ->
                    val itemId = document.id
                    val itemName = document.getString("name") ?: ""
                    val brandId = document.getString("brandId") ?: ""
                    val categoryId = document.getString("categoryId") ?: ""

                    loadBrandName(brandId) { brandName ->
                        loadCategoryName(categoryId) { categoryName ->
                            val item = Item(
                                id = itemId,
                                name = itemName,
                                brand = brandName,
                                category = categoryName,
                                description = document.getString("description")?.replace("\"", "")
                                    ?: "",
                                ingredients = listOf(ingredient?.name),
                                image = document.getString("imageURL") ?: ""
                            )
                            items.add(item)

                            if (items.size == documents.size) {
                                result(items)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                errorMessage.value = exception.localizedMessage
                result(null)
            }
    }

    fun fetchNewItems(result: (List<Item>?) -> Unit) {
        val itemsCollection = db.collection(COLLECTION_NEW)
        itemsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val items = mutableListOf<Item>()

                querySnapshot.documents.forEach { document ->
                    val itemId = document.id
                    val itemName = document.getString("name") ?: ""
                    val brandId = document.getString("brandId") ?: ""
                    val categoryId = document.getString("categoryId") ?: ""
                    val ingredientIds =
                        document.get("ingredientIds") as? List<String> ?: emptyList()

                    loadBrandName(brandId) { brandName ->
                        loadCategoryName(categoryId) { categoryName ->
                            loadIngredientNames(ingredientIds) { ingredientNames ->
                                val item = Item(
                                    id = itemId,
                                    name = itemName,
                                    brand = brandName,
                                    category = categoryName,
                                    ingredients = ingredientNames
                                )
                                items.add(item)

                                if (items.size == querySnapshot.size()) {
                                    result(items)
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                errorMessage.value = exception.localizedMessage
                result(null)
            }
    }


    /**
     * brandsコレクションからドキュメントIDをキーにしてデータを取得する
     * @args result ブランド名
     */
    private fun loadBrandName(
        brandId: String,
        result: (String?) -> Unit
    ) {
        loadData(
            collectionPath = COLLECTION_BRANDS,
            onSuccess = { querySnapshot ->
                val brandName = querySnapshot.documents
                    .firstOrNull { it.id == brandId }
                    ?.getString("name")
                result(brandName)
            }
        )
    }

    /**
     * CategoryコレクションからドキュメントIDをキーにしてデータを取得する
     * @args result カテゴリ名
     */
    private fun loadCategoryName(
        categoryId: String,
        result: (String?) -> Unit
    ) {
        loadData(
            collectionPath = COLLECTION_CATEGORIES,
            onSuccess = { querySnapshot ->
                val categoryName = querySnapshot.documents
                    .firstOrNull { it.id == categoryId }
                    ?.getString("name")
                result(categoryName)
            }
        )
    }

    /**
     * IngredientコレクションからドキュメントIDをキーにしてデータを取得する
     * @args result 成分名のリスト
     */
    private fun loadIngredientNames(
        ingredientIds: List<String>,
        result: (List<String>?) -> Unit
    ) {
        loadData(
            collectionPath = COLLECTION_INGREDIENTS,
            onSuccess = { querySnapshot ->
                val ingredientNames = querySnapshot.documents
                    .filter { it.id in ingredientIds }
                    .mapNotNull { it.getString("name") }
                result(ingredientNames)
            }
        )
    }

    /**
     * brandsコレクションからすべてのブランドを取得するメソッド
     * @args result List<Brand>
     */
    fun loadBrands(result: (List<Brand>) -> Unit) {
        loadData(
            collectionPath = COLLECTION_BRANDS,
            onSuccess = { querySnapshot ->
                result(querySnapshot.documents.mapNotNull {
                    val name = it.getString("name")
                    if (name != null) Brand(it.id, name) else null
                })
            }
        )
    }

    /**
     * Categoryコレクションからすべてのドキュメントを取得するメソッド
     * @args result List<Brand>
     */
    fun loadCategories(result: (List<Category>) -> Unit) {
        loadData(
            collectionPath = COLLECTION_CATEGORIES,
            onSuccess = { querySnapshot ->
                result(querySnapshot.documents.mapNotNull {
                    val name = it.getString("name")
                    if (name != null) Category(it.id, name) else null
                })
            }
        )
    }


    /**
     * Ingredientコレクションからすべてのドキュメントを取得するメソッド
     * @args result List<Ingredient>
     */
    fun loadIngredients(result: (List<Ingredient>) -> Unit) {
        loadData(
            collectionPath = COLLECTION_INGREDIENTS,
            onSuccess = { querySnapshot ->
                result(querySnapshot.documents.mapNotNull {
                    val name = it.getString("name")
                    if (name != null) Ingredient(it.id, name) else null
                })
            }
        )
    }

    /**
     * コレクションパスをキーにFirestoreからすべてのドキュメントを取得する
     * @args collectionPath コレクションパス
     * @args onSuccess 取得成功時のコールバック
     */
    private inline fun loadData(
        collectionPath: String,
        crossinline onSuccess: (QuerySnapshot) -> Unit
    ) {
        db.collection(collectionPath).get()
            .addOnSuccessListener { querySnapshot ->
                onSuccess(querySnapshot)
            }
            .addOnFailureListener { exception ->
                // 取得に失敗した場合はダイアログを表示する
                errorMessage.value = exception.localizedMessage
            }
    }

    /**
     * コレクションパス、ドキュメントIDを指定してFirestoreにデータを保存する
     * @args collectionPath コレクションパス
     * @args documentId ドキュメントID
     * @args data 保存するデータ
     */
    fun saveData(
        collectionPath: String,
        documentId: String,
        data: Any
    ) {
        db.collection(collectionPath).document(documentId)
            .set(data)
            .addOnSuccessListener {
                Log.d(
                    "FirestoreApp",
                    "DocumentSnapshot added with ID: $documentId"
                )
            }
            .addOnFailureListener { exception ->
                errorMessage.value = exception.localizedMessage
            }
    }
}