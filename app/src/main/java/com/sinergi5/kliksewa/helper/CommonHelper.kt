package com.sinergi5.kliksewa.helper

import android.content.Context
import java.text.NumberFormat
import java.util.Locale

object CommonHelper {
    fun toast(context: Context, message: String) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    fun formatPrice(price: Int, locale: Locale = Locale("id", "ID")): String {
        val numberFormat = NumberFormat.getCurrencyInstance(locale)
        numberFormat.maximumFractionDigits = 0 // Menghilangkan dua angka desimal
        return numberFormat.format(price)
    }
}