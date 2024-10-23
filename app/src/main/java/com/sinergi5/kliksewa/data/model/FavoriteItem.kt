package com.sinergi5.kliksewa.data.model

import com.google.firebase.Timestamp

data class FavoriteItem (
    var itemId: String = "",
    var quantity: Int = 1,
    var addedAt: Timestamp? = null
)