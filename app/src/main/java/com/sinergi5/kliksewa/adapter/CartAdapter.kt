package com.sinergi5.kliksewa.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sinergi5.kliksewa.databinding.ItemProductLayoutCartBinding
import com.sinergi5.kliksewa.model.CartItem

class CartAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<CartItem>) = differ.submitList(list)

    inner class CartViewHolder(private val binding: ItemProductLayoutCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.apply {
                tvProductName.text = cartItem.productName
                tvProductPrice.text = "Rp ${String.format("%,d", cartItem.pricePerItem)}"
                tvQuantity.text = cartItem.quantity.toString()

                // Setup quantity controls
                btnQuantityMin.setOnClickListener {
                    if (cartItem.quantity > 1) {
                        onQuantityChanged(cartItem, cartItem.quantity - 1)
                    }
                }

                btnQuantityPlus.setOnClickListener {
                    onQuantityChanged(cartItem, cartItem.quantity + 1)
                }

                // Load image if available (you'll need to implement this)
                // Glide.with(itemView.context)
                //     .load(cartItem.imageUrl)
                //     .into(ivProductImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemProductLayoutCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() = differ.currentList.size
}