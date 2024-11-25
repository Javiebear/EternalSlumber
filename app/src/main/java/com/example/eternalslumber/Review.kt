package com.example.eternalslumber

data class Review(
    val reviewId: Int,
    val title: String,
    val rating: Int,
    val description: String,
    val userId: Int,
    val propertyId: Int
)