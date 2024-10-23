package com.sinergi5.kliksewa.data.model

data class StoreAddress (
    val street: String,
    val city: String,
    val province: String,
    val postalCode: String,
    val detail: String,
    val coordinates: Coordinates? = null
)


data class Coordinates(
    val latitude: Double,
    val longitude: Double
)