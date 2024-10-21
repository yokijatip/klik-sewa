package com.sinergi5.kliksewa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sinergi5.kliksewa.data.model.CategoryItem
import com.sinergi5.kliksewa.databinding.ListItemCategoryHorizontalBinding

class CategoryItemAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<CategoryItem, CategoryItemAdapter.CategoryItemViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryItemAdapter.CategoryItemViewHolder {
        val binding = ListItemCategoryHorizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CategoryItemAdapter.CategoryItemViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position), onItemClick)
    }

    inner class CategoryItemViewHolder(private val binding: ListItemCategoryHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryItem: CategoryItem, onItemClick: (String) -> Unit) {
            binding.tvCategory.text = categoryItem.name

            binding.root.setOnClickListener {
                categoryItem.id?.let {
                    onItemClick(it)
                }
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem == newItem
        }
    }
}