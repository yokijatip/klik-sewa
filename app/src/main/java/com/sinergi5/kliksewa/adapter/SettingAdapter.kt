package com.sinergi5.kliksewa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.data.model.Setting

class SettingAdapter(
    private val settingList: List<Setting>,
    private val onSettingClick: (Setting) -> Unit
) : RecyclerView.Adapter<SettingAdapter.SettingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SettingAdapter.SettingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_setting, parent, false
        )
        return SettingViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingAdapter.SettingViewHolder, position: Int) {
        holder.bind(settingList[position])
    }

    override fun getItemCount(): Int {
        return settingList.size
    }

    inner class SettingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val settingName: TextView = itemView.findViewById(R.id.tv_setting)
        private val menuIcon: ImageView = itemView.findViewById(R.id.iv_setting)

        fun bind(setting: Setting) {
            settingName.text = setting.title
            menuIcon.setImageResource(setting.icon)

            itemView.setOnClickListener { onSettingClick(setting) }
        }

    }

}