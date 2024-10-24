package com.sinergi5.kliksewa.data.model

data class CartItem(
    val id: String = "",
    val item: Item = Item(),
    val quantity: Int = 0
)