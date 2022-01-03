package com.example.transitlogger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.transitlogger.R
import com.example.transitlogger.db.Trip
import com.example.transitlogger.other.TrackingUtility
import kotlinx.android.synthetic.main.item_trip.view.*
import java.text.SimpleDateFormat
import java.util.*

class TripAdapter:RecyclerView.Adapter<TripAdapter.TripViewHolder> (){
    inner class TripViewHolder (itemView: View):RecyclerView.ViewHolder(itemView)
    init{
        //itemView.setonclick

    }
    val diffCallback=object: DiffUtil.ItemCallback<Trip>(){
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem.hashCode()==newItem.hashCode()
        }
    }
    val differ =AsyncListDiffer(this,diffCallback)
    fun submitList(list:List<Trip>)=differ.submitList(list)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        return TripViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_trip,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip=differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(trip.img).into(ivTripImage)

            val calendar = Calendar.getInstance().apply{
                timeInMillis=trip.timestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy",Locale.getDefault())
            tvDate.text=dateFormat.format(calendar.time)

            val avgSpeed="${trip.avgSpeed} km/h"
            tvAvgSpeed.text=avgSpeed

            val distanceInKm="${trip.distm/1000f} km"
            tvDistance.text=distanceInKm

            tvTime.text=TrackingUtility.getFormattedStopWatchTime(trip.timeinmillis)

        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}