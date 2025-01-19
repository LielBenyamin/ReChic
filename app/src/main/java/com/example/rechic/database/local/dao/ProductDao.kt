package com.example.rechic.database.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rechic.database.local.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE ownerUid = :ownerUid")
    fun getProductsByOwnerid(ownerUid: String): Flow<List<ProductEntity>>

    @Query("DELETE FROM products")
    suspend fun clearProducts()

    @Query("SELECT * FROM products")
    suspend fun getAllProductsOnce(): List<ProductEntity> // Fetch products once for comparison

    @Delete
    suspend fun deleteProducts(products: List<ProductEntity>)
}