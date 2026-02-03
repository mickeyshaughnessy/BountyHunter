package com.bountyhunter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bountyhunter.R
import com.bountyhunter.models.RecurringWorkout

class RecurringAdapter : ListAdapter<RecurringWorkout, RecurringAdapter.ViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recurring, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvSchedule: TextView = itemView.findViewById(R.id.tvSchedule)
        
        fun bind(recurring: RecurringWorkout) {
            tvTitle.text = recurring.title
            tvSchedule.text = "📅 ${recurring.schedule.capitalize()}"
        }
    }
    
    private class DiffCallback : DiffUtil.ItemCallback<RecurringWorkout>() {
        override fun areItemsTheSame(oldItem: RecurringWorkout, newItem: RecurringWorkout): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: RecurringWorkout, newItem: RecurringWorkout): Boolean {
            return oldItem == newItem
        }
    }
}
