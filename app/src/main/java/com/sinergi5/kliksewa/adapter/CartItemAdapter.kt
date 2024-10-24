package com.sinergi5.kliksewa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sinergi5.kliksewa.data.model.CartItem
import com.sinergi5.kliksewa.databinding.ListItemCartBinding
import com.sinergi5.kliksewa.helper.CommonHelper

class CartItemAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onRemoveClicked: (CartItem) -> Unit
) : ListAdapter<CartItem, CartItemAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ListItemCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ListItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.apply {
                // Bind item details
                tvItemName.text = cartItem.item.name
                tvItemOwner.text = cartItem.item.ownerId
                tvItemPrice.text = "${cartItem.item.priceDay?.let { CommonHelper.formatPrice(it) }} / Price per day"
                tvItemQuantity.text = cartItem.quantity.toString()

                // Load image using Glide
                if (cartItem.item.imageUrls!!.isNotEmpty()) {
                    Glide.with(root.context)
                        .load(cartItem.item.imageUrls!![0])
                        .centerCrop()
                        .into(ivItemImage)
                }

                // Handle quantity changes
                btnPlus.setOnClickListener {
                    val newQuantity = cartItem.quantity + 1
                    onQuantityChanged(cartItem, newQuantity)
                }

                btnMinus.setOnClickListener {
                    val newQuantity = cartItem.quantity - 1
                    if (newQuantity >= 0) {
                        onQuantityChanged(cartItem, newQuantity)
                    }
                }

                // Handle remove item
                btnDelete.setOnClickListener {
                    onRemoveClicked(cartItem)
                }
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}