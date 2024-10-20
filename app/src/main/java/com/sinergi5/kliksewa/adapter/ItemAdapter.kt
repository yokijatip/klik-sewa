package com.sinergi5.kliksewa.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.data.model.Item
import com.sinergi5.kliksewa.databinding.ListItemItemBinding

class ItemAdapter(private val onItemClicked: (Item) -> Unit) :
    ListAdapter<Item, ItemAdapter.ItemViewHolder>(ItemDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ItemViewHolder {
        val binding = ListItemItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemAdapter.ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: ListItemItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.apply {
                val imageUrl = item.imageUrls?.firstOrNull()
                val pricePerDay = itemView.context.getString(R.string.price_per_day)
                tvItemName.text = item.name
                tvItemType.text = item.type
//                tvItemPrice.text = "Rp ${item.price_per_day} / $pricePerDay"
                val price = item.priceDay.toString()
                tvItemPrice.text = "Rp $price / $pricePerDay"
                Log.d("ItemAdapter", "Image URL: ${imageUrl}")
                Glide.with(root.context)
                    .load(item.imageUrls?.firstOrNull())
                    .into(ivItem)

                root.setOnClickListener {
                    onItemClicked(item)
                }

            }
        }
    }

    // Callback to calculate the differences between the old and new list
    class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            // Compare item ID or unique field to check if they are the same
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            // Compare item content to check if they have the same data
            return oldItem == newItem
        }
    }
}