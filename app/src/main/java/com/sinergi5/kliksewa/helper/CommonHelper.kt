package com.sinergi5.kliksewa.helper

import android.content.Context

object CommonHelper {
    fun toast(context: Context, message: String) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}