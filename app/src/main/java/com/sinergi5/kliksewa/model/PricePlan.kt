package com.sinergi5.kliksewa.model

data class PricePlan(
    val type: String,
    val price: Long,
    var isSelected: Boolean = false
)