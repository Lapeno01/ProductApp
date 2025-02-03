package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.entities.Header
import com.example.model.entities.Product
import com.example.model.entities.toDomainModel
import com.example.model.entities.toEntity
import com.example.model.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductViewModel(
    application: Application,
    private val repository: ProductRepository
) : AndroidViewModel(application) {

    private val _filter = MutableStateFlow("Alle")  // "Alle", "Verfügbar", "Vorgemerkt"
    // val filter: StateFlow<String> = _filter

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _header = MutableStateFlow<Header?>(null)
    val header: StateFlow<Header?> = _header

    init {
        fetchProducts()
    }

    // Automatically whenever the DB changes
    private val allLocalProductsFlow: Flow<List<Product>> =
        repository.getAllProductsFlow().map { entities ->
                entities.map { it.toDomainModel() }
        }

    val filteredProducts: StateFlow<List<Product>> =
        combine(allLocalProductsFlow, _filter) { allProducts, selectedFilter ->
            when (selectedFilter) {
                "Alle" -> allProducts
                "Verfügbar" -> allProducts.filter { it.available }
                "Vorgemerkt" -> allProducts.filter { it.marked }
                else -> allProducts
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun getProductFlow(productId: Int): Flow<Product> {
        return repository.getProductFlow(productId)
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            repository.fetchProductResponse()
                .catch { e ->
                    _errorMessage.value = "Error fetching products: ${e.message}"
                }
                .collect { response ->
                    _header.value = response.header
                    _errorMessage.value = null

                    // Merge logic
                    mergeServerProductsIntoDb(response.products)
                }
        }
    }

    fun refreshProducts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.fetchProductResponse()
                    .catch { e ->
                        _errorMessage.value = "Error refreshing products: ${e.message}"
                    }
                    .collect { response ->
                        _header.value = response.header
                        _errorMessage.value = null

                        mergeServerProductsIntoDb(response.products)
                    }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private suspend fun mergeServerProductsIntoDb(serverProducts: List<Product>) {
        val existingEntities = repository.getAllProductsOnce()
        val existingMap = existingEntities.associateBy { it.id }

        val mergedEntities = serverProducts.map { serverProduct ->
            val oldEntity = existingMap[serverProduct.id]
            serverProduct.toEntity().copy(
                marked = oldEntity?.marked ?: serverProduct.marked
            )
        }

        repository.insertProducts(mergedEntities)
    }

    fun toggleMark(product: Product) {
        val newMarked = !product.marked
        val updatedProduct = product.copy(marked = newMarked)
        viewModelScope.launch {
            repository.updateProduct(updatedProduct.toEntity())
        }
    }

    fun updateFilter(newFilter: String) {
        _filter.value = newFilter
    }
}
