package com.sinergi5.kliksewa.model

data class CartItem(
    val id: String = "", // Cart item ID
    val productId: String = "",
    val productName: String = "",
    val userId: String = "",
    val quantity: Int = 0,
    val pricePerItem: Long = 0,
    val totalPrice: Long = 0,
    val createdAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
    val updatedAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)

