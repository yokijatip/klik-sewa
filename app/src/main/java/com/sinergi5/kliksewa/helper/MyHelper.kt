package com.sinergi5.kliksewa.helper

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

object MyHelper {
    fun showMessages(message: String, context: Context)  {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // Comprehensive Snackbar method with multiple options
    fun showSnackBar(
        view: View,
        message: String,
        duration: Int = Snackbar.LENGTH_SHORT,
        actionText: String? = null,
        actionColor: Int? = null,
        onActionClick: (() -> Unit)? = null
    ): Snackbar {
        val snackbar = Snackbar.make(view, message, duration)

        // Add action if provided
        actionText?.let { text ->
            snackbar.setAction(text) {
                onActionClick?.invoke()
            }

            // Set custom action color if provided
            actionColor?.let { color ->
                snackbar.setActionTextColor(color)
            }
        }

        snackbar.show()
        return snackbar
    }

    // Overloaded method for simple Snackbar without action
    fun showSnackBar(
        view: View,
        message: String,
        duration: Int = Snackbar.LENGTH_SHORT
    ): Snackbar {
        return showSnackBar(view, message, duration, null, null, null)
    }

    // Error Snackbar with red color
    fun showErrorSnackBar(
        view: View,
        errorMessage: String,
        actionText: String? = "Retry",
        onRetry: (() -> Unit)? = null
    ): Snackbar {
        return showSnackBar(
            view = view,
            message = errorMessage,
            duration = Snackbar.LENGTH_LONG,
            actionText = actionText,
            actionColor = android.R.color.holo_red_light,
            onActionClick = onRetry
        )
    }

    // Success Snackbar with green color
    fun showSuccessSnackBar(
        view: View,
        successMessage: String
    ): Snackbar {
        return showSnackBar(
            view = view,
            message = successMessage,
            duration = Snackbar.LENGTH_SHORT,
            actionText = "OK",
            actionColor = android.R.color.holo_green_dark
        )
    }
}