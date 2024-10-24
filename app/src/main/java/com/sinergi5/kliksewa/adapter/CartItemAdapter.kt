package com.sinergi5.kliksewa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sinergi5.kliksewa.data.model.Item
import com.sinergi5.kliksewa.databinding.ListItemCartBinding
import com.sinergi5.kliksewa.helper.CommonHelper

class CartItemAdapter(
    private val items: List<Item>,
    private val onDeleteClick: (Item) -> Unit,
    private val onAddClick: (Item) -> Unit,
    private val onMinusClick: (Item) -> Unit,
    private val onItemClick: (Item) -> Unit
)  : RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartItemAdapter.CartItemViewHolder {
        val binding = ListItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemAdapter.CartItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CartItemViewHolder(private val binding: ListItemCartBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.apply {
                tvItemName.text = item.name
                tvItemOwner.text = item.ownerId
                tvItemPrice.text = item.priceDay?.let { CommonHelper.formatPrice(it) }


                btnDelete.setOnClickListener { onDeleteClick(item) }
                btnPlus.setOnClickListener { onAddClick(item) }
                btnMinus.setOnClickListener { onMinusClick(item) }
                binding.root.setOnClickListener {
                    onItemClick(item)
                }


            }
        }
    }
}