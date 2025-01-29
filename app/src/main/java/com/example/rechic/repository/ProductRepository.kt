package com.example.rechic.repository

import com.example.rechic.database.local.dao.ProductDao
import com.example.rechic.database.local.dao.UserProfileDao
import com.example.rechic.database.local.entities.ProductEntity
import com.example.rechic.database.remote.ProductFirebaseDB
import com.example.rechic.model.ProductWithUserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(
    private val productFirebaseDB: ProductFirebaseDB,
    private val productDao: ProductDao,
    private val userDao: UserProfileDao,
) {

    suspend fun saveProduct(product: ProductEntity) {
        productFirebaseDB.saveProduct(product)
        productDao.insertProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        productFirebaseDB.deleteProductFromFirestore(product.productDocumentId)
        productDao.deleteProducts(listOf(product))
    }

    suspend fun updateProduct(
        product: ProductEntity,
        productId: String,
        keys: List<String>,
        values: List<Any>,
    ) {
        val fieldsToUpdate = keys.zip(values).toMap()
        productFirebaseDB.updateProductIdFields(productId, fieldsToUpdate)
        productDao.insertProduct(product)
    }

    fun getAllProductsFlow(): Flow<List<ProductWithUserProfile>> {
        return productDao.getAllProducts().map { products ->
            mapProductsToProductWithUserProfile(products)
        }
    }

    // Function to get all products by ownerId and map them to ProductWithUserProfile
    fun getAllProductsWithUserProfileByOwnerIdFlow(ownerUid: String): Flow<List<ProductWithUserProfile>> {
        return productDao.getProductsByOwnerid(ownerUid).map { products ->
            mapProductsToProductWithUserProfile(products)
        }
    }

    private suspend fun mapProductsToProductWithUserProfile(products: List<ProductEntity>): List<ProductWithUserProfile> {
        return products.mapNotNull { productEntity ->
            val user = userDao.getUserProfileByUid(productEntity.ownerUid)
            user?.let {
                ProductWithUserProfile(
                    product = productEntity,
                    userProfile = user
                )
            }
        }
    }

    suspend fun saveProductsToLocal(products: List<ProductEntity>) {
        productDao.insertProducts(products)
    }

    suspend fun syncProducts() {
        try {
            val firestoreProducts = productFirebaseDB.fetchAllProductsFromFirestore()
            productDao.insertProducts(firestoreProducts)
            val localProducts = productDao.getAllProductsOnce()
            val outdatedProducts = localProducts.filter { localProduct ->
                firestoreProducts.none { it.productDocumentId == localProduct.productDocumentId }
            }
            if (outdatedProducts.isNotEmpty()) {
                productDao.deleteProducts(outdatedProducts)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}