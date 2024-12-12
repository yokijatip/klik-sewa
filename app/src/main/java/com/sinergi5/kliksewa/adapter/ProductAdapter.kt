package com.sinergi5.kliksewa.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.databinding.ItemProductLayoutBinding
import com.sinergi5.kliksewa.model.Product

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var products = mutableListOf<Product>()
    private var onItemClick: ((Product) -> Unit)? = null
    private var onFavoriteClick: ((Product, Int) -> Unit)? = null
    private var currentUserId: String? = null

    fun setOnItemClickListener(listener: (Product) -> Unit) {
        onItemClick = listener
    }

    fun setOnFavoriteClickListener(listener: (Product, Int) -> Unit) {
        onFavoriteClick = listener
    }

    fun updateFavoriteStatus(position: Int, userId: String) {
        products[position].let { product ->
            val newFavoriteBy = if (userId in product.favoriteBy) {
                product.favoriteBy - userId
            } else {
                product.favoriteBy + userId
            }
            products[position] = product.copy(favoriteBy = newFavoriteBy)
            notifyItemChanged(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCurrentUserId(userId: String?) {
        currentUserId = userId
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<Product>) {
        products = newList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductAdapter.ProductViewHolder {
        val binding = ItemProductLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductAdapter.ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size

    inner class ProductViewHolder(
        private val binding: ItemProductLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            binding.apply {
                tvProductName.text = product.name
                tvProductPrice.text = "Rp ${product.pricePerDay}"

                val firstImageUrl = product.imageUrl.firstOrNull()
                if (firstImageUrl != null) {
                    Glide.with(itemView)
                        .load(firstImageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(ivProductImage)
                } else {
                    ivProductImage.setImageResource(R.drawable.placeholder_image)
                }

                val isFavorited = currentUserId?.let { userId ->
                    product.favoriteBy.contains(userId)
                } ?: false

                favoriteIcon.setImageResource(
                    if (isFavorited) R.drawable.ic_menu_heart_filled
                    else R.drawable.ic_menu_heart
                )

                root.setOnClickListener { onItemClick?.invoke(product) }
                favoriteButton.setOnClickListener { onFavoriteClick?.invoke(product, adapterPosition) }
            }
        }
    }


}