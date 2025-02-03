package com.example.model.entities

import java.io.Serializable

data class Product(
    val id: Int,
    val name: String,
    val type: String,
    val color: String,
    val imageURL: String,
    val colorCode: String,
    val available: Boolean,
    val releaseDate: Long,
    val description: String,
    val longDescription: String,
    val rating: Double,
    val price: Price,
    var marked: Boolean = false
): Serializable

data class Price(
    val value: Double,
    val currency: String
): Serializable

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        type = type,
        color = color,
        imageURL = imageURL,
        colorCode = colorCode,
        available = available,
        releaseDate = releaseDate,
        description = description,
        longDescription = longDescription,
        rating = rating,
        priceValue = price.value,
        priceCurrency = price.currency,
        marked = marked
    )
}