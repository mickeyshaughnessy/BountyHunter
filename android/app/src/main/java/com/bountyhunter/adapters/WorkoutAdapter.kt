package com.bountyhunter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bountyhunter.R
import com.bountyhunter.models.Workout
import java.text.SimpleDateFormat
import java.util.*

class WorkoutAdapter : ListAdapter<Workout, WorkoutAdapter.ViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvType: TextView = itemView.findViewById(R.id.tvType)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        private val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)
        
        fun bind(workout: Workout) {
            tvTitle.text = workout.title
            tvType.text = workout.type.capitalize()
            tvDuration.text = workout.duration
            
            // Format date
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                val date = inputFormat.parse(workout.timestamp.substringBefore("."))
                tvDate.text = outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                tvDate.text = workout.timestamp.substringBefore("T")
            }
            
            // Show notes if available
            val notes = workout.data?.notes
            if (!notes.isNullOrEmpty()) {
                tvNotes.visibility = View.VISIBLE
                tvNotes.text = notes
            } else {
                tvNotes.visibility = View.GONE
            }
        }
    }
    
    private class DiffCallback : DiffUtil.ItemCallback<Workout>() {
        override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            return oldItem == newItem
        }
    }
}
