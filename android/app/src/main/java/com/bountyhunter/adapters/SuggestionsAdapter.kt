package com.bountyhunter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bountyhunter.R
import com.bountyhunter.models.Suggestion

class SuggestionsAdapter(
    private val onUseClick: (Suggestion) -> Unit
) : ListAdapter<Suggestion, SuggestionsAdapter.ViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggestion, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvType: TextView = itemView.findViewById(R.id.tvType)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        private val btnUse: Button = itemView.findViewById(R.id.btnUse)
        
        fun bind(suggestion: Suggestion) {
            tvTitle.text = suggestion.title
            tvType.text = suggestion.type.capitalize()
            tvDuration.text = suggestion.duration
            
            btnUse.setOnClickListener {
                onUseClick(suggestion)
            }
        }
    }
    
    private class DiffCallback : DiffUtil.ItemCallback<Suggestion>() {
        override fun areItemsTheSame(oldItem: Suggestion, newItem: Suggestion): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Suggestion, newItem: Suggestion): Boolean {
            return oldItem == newItem
        }
    }
}
