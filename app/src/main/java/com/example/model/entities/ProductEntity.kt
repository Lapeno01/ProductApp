package com.example.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
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
    val priceValue: Double,
    val priceCurrency: String,
    val marked: Boolean = false
)

fun ProductEntity.toDomainModel(): Product {
    return Product(
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
        price = Price(
            value = priceValue,
            currency = priceCurrency
        ),
        marked = marked
    )
}