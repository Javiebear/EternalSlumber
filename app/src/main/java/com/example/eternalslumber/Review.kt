<<<<<<< HEAD
package com.example.eternalslumber

data class Review(
    val reviewId: Int,
    val title: String,
    val description: String,
    val user: String,
    val propertyId: Int
=======
package com.example.eternalslumber

data class Review(
    val reviewId: Int,
    val title: String,
    val rating: Int,
    val description: String,
    val userId: Int,
    val propertyId: Int
>>>>>>> 4ca8102e81a4b3b45e97fd1d570fc34cff4494ad
)