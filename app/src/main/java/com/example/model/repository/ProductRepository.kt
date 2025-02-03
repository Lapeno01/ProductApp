package com.example.model.repository

import com.example.model.api.ProductApi
import com.example.model.db.ProductDao
import com.example.model.entities.Product
import com.example.model.entities.ProductEntity
import com.example.model.entities.ProductResponse
import com.example.model.entities.toDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ProductRepository(
    private val api: ProductApi,
    private val dao: ProductDao
) {
    private var requestCount = 0

    fun getAllProductsFlow() = dao.getAllProducts()

    suspend fun getAllProductsOnce(): List<ProductEntity> = dao.getAllProductsOnce()

    suspend fun insertProducts(products: List<ProductEntity>) = dao.insertProducts(products)

    suspend fun updateProduct(product: ProductEntity) = dao.updateProduct(product)

    fun getProductFlow(productId: Int): Flow<Product> {
        return dao.getProductById(productId).map { it.toDomainModel() }
    }

    fun fetchProductResponse(): Flow<ProductResponse> = flow {
        requestCount += 1
        if (requestCount % 3 == 0) {
            throw Exception("Simulated error for request count: $requestCount")
        } else {
            val response = api.getProducts()
            emit(response)
        }
    }
}
