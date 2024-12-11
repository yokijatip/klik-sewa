package com.sinergi5.kliksewa.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.databinding.ItemPricePlanLayoutBinding
import com.sinergi5.kliksewa.model.PricePlan

class PricePlanAdapter(
    private val onPricePlanSelected: (PricePlan) -> Unit
) : RecyclerView.Adapter<PricePlanAdapter.PricePlanViewHolder>() {

    private var pricePlans = listOf<PricePlan>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(plans: List<PricePlan>) {
        pricePlans = plans
        notifyDataSetChanged()
    }

    fun updateSelectedPlan(position: Int) {
        // Reset semua selection
        pricePlans.forEachIndexed { index, plan ->
            if (plan.isSelected && index != position) {
                plan.isSelected = false
                notifyItemChanged(index)
            }
        }
        // Set yang terpilih
        pricePlans[position].isSelected = true
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PricePlanViewHolder {
        val binding = ItemPricePlanLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PricePlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PricePlanViewHolder, position: Int) {
        holder.bind(pricePlans[position])
    }

    override fun getItemCount() = pricePlans.size

    inner class PricePlanViewHolder(
        private val binding: ItemPricePlanLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pricePlan: PricePlan) {
            binding.apply {
                // Set text berdasarkan tipe
                tvPricePlan.text = when(pricePlan.type) {
                    "hour" -> "Per Hour"
                    "day" -> "Per Day"
                    "week" -> "Per Week"
                    "month" -> "Per Month"
                    else -> "Per Day"
                }

                // Set border color berdasarkan status selected
                cvPricePlan.strokeColor = if(pricePlan.isSelected) {
                    ContextCompat.getColor(itemView.context, R.color.black)
                } else {
                    ContextCompat.getColor(itemView.context, R.color.soft_gray)
                }

                // Handle click
                root.setOnClickListener {
                    updateSelectedPlan(adapterPosition)
                    onPricePlanSelected(pricePlan)
                }
            }
        }
    }
}