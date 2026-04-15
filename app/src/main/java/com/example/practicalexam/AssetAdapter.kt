package com.example.practicalexam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AssetAdapter(
    private val assetList: List<Asset>,
    private val onItemClicked: (Asset) -> Unit
) : RecyclerView.Adapter<AssetAdapter.AssetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.asset_list_item, parent, false)
        return AssetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val currentItem = assetList[position]
        holder.assetNumberTextView.text = currentItem.assetNumber
        holder.assetDescriptionTextView.text = currentItem.description
        holder.itemView.setOnClickListener {
            onItemClicked(currentItem)
        }
    }

    override fun getItemCount() = assetList.size

    class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val assetNumberTextView: TextView = itemView.findViewById(R.id.assetNumberTextView)
        val assetDescriptionTextView: TextView = itemView.findViewById(R.id.assetDescriptionTextView)
    }
}
