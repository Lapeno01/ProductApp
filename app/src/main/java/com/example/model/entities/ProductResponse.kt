package com.example.model.entities

data class ProductResponse(
    val header: Header,
    val filters: List<String>,
    val products: List<Product>
)
