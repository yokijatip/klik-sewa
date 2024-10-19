package com.sinergi5.kliksewa.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        // Tambahkan jarak di antara item, tetapi tidak di tepi kiri dan kanan
        if (position == 0) {
            outRect.left = 0 // Tidak ada jarak di tepi kiri untuk item pertama
        } else {
            outRect.left = space // Jarak antar item
        }

        if (position == itemCount - 1) {
            outRect.right = 0 // Tidak ada jarak di tepi kanan untuk item terakhir
        }
    }
}
