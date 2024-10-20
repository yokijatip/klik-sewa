package com.sinergi5.kliksewa.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class Item(
    var itemId: String? = "",
    var availability: Boolean? = null,
    var categoryId: String? = "",
    var description: String? = "",
    var dueDate: Timestamp? = null,
    var imageUrls: ArrayList<String>? = null,
    var name: String? = "",
    var ownerId: String? = "",
    var priceDay: Int? = null,
    var rentedAt: Timestamp? = null,
    var rentedId: String? = "",
    var type: String? = ""
) : Parcelable
