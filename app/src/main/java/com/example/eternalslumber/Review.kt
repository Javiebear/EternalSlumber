package com.example.eternalslumber

data class Review(
    val reviewId: Int,
    val title: String,
    val description: String,
    val user: String,
    val propertyId: Int
)