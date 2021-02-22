package com.app.foodplace.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.domain.data.Place
import com.app.foodplace.R

class LocationsAdapter(private val context: Context) :
    RecyclerView.Adapter<LocationsAdapter.LocationViewHolder>() {
    private var data: List<Place> = listOf()

    fun notifyData(data: List<Place>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.location_item, parent, false)
        return LocationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.name.text = data[position].name
    }

    override fun getItemCount(): Int = data.size

    class LocationViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.name)
    }
}