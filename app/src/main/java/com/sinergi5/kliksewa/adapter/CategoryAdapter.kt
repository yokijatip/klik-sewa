package com.sinergi5.kliksewa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.data.Category

class CategoryAdapter(
    private val categoryList: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val menuName: TextView = itemView.findViewById(R.id.tv_category)
        private val menuIcon: ImageView = itemView.findViewById(R.id.iv_category)

        fun bind(category: Category) {
            menuName.text = category.name
            menuIcon.setImageResource(category.icon)

            itemView.setOnClickListener { onCategoryClick(category) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapter.CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.CategoryViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

}