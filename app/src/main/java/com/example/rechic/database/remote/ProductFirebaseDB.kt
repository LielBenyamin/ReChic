package com.example.rechic.database.remote

import com.example.rechic.database.local.entities.ProductEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductFirebaseDB(private val firestore: FirebaseFirestore) {

    suspend fun saveProduct(product: ProductEntity) {
        try {
            val productId =
                firestore.collection(FirestoreConstants.PRODUCTS_COLLECTION).document().id
            firestore.collection(FirestoreConstants.PRODUCTS_COLLECTION)
                .document(productId)
                .set(
                    product.copy(
                        productDocumentId = productId,
                    )
                )
                .await()
        } catch (e: Exception) {

        }
    }

    suspend fun updateProductIdFields(productId: String, fields: Map<String, Any>) {
        try {
            val userRef =
                firestore.collection(FirestoreConstants.PRODUCTS_COLLECTION).document(productId)
            userRef.update(fields).await()
        } catch (e: Exception) {
        }
    }

    suspend fun deleteProductFromFirestore(productId: String): Boolean {
        return try {
            firestore.collection(FirestoreConstants.PRODUCTS_COLLECTION).document(productId)
                .delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun fetchAllProductsFromFirestore(): List<ProductEntity> {
        return try {
            val snapshot = firestore.collection(FirestoreConstants.PRODUCTS_COLLECTION)
                .get()
                .await()
            snapshot.toObjects(ProductEntity::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

}