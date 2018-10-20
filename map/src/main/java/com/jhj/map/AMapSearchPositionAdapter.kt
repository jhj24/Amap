package com.jhj.map

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amap.api.services.help.Tip
import kotlinx.android.synthetic.main.list_item_amap_position_search.view.*

/**
 * Created by jhj on 18-9-19.
 */
class AMapSearchPositionAdapter(private val activity: AMapActivity) : RecyclerView.Adapter<AMapSearchPositionAdapter.ItemViewHolder>() {

    var dataList = arrayListOf<Tip>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.list_item_amap_position_search, parent, false)
        return ItemViewHolder(view)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemView?.let {
            it.tv_amap_position_item.text = dataList[position].name
            it.tv_amap_district_item.text = dataList[position].district
            holder.itemView.setOnClickListener {
                activity.searchPositionOnMap(dataList[position])
            }
        }
    }


    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}