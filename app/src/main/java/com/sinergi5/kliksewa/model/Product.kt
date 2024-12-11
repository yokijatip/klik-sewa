package com.sinergi5.kliksewa.model

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: List<String> = emptyList(),
    val ownerId: String = "",
    val categoryId: String = "",
    val pricePerHour: Long = 0,
    val pricePerDay: Long = 0,
    val pricePerWeek: Long = 0,
    val pricePerMonth: Long = 0,
    val isAvailable: Boolean = true,
    val location: String = "",
    val favoriteBy: List<String> = emptyList(),
    val rating: Double = 0.0,
    val totalRating: Int = 0,
    val ratingBy: List<String> = emptyList(),
    val createdAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
    val updatedAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)
