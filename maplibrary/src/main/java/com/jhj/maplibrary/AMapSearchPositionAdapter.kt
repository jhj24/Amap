package com.jhj.maplibrary

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.amap.api.services.help.Tip

/**
 * Created by jhj on 18-9-19.
 */
class AMapSearchPositionAdapter(val activity: Amap) : RecyclerView.Adapter<AMapSearchPositionAdapter.ItemViewHolder>() {

    var dataList = arrayListOf<Tip>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.layout_amap_position_search_item, parent, false)
        return ItemViewHolder(view)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemView?.let {
            it.findViewById<TextView>(R.id.tv_amap_position_item).text = dataList[position].name
            it.findViewById<TextView>(R.id.tv_amap_district_item).text = dataList[position].district
            holder.itemView.setOnClickListener {
                activity.removeMap(dataList[position])
            }
        }
    }


    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}