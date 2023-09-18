package com.mashood.thesaurus.search.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mashood.thesaurus.databinding.ItemWordSuggestionBinding

class SuggestionAdapter(private val listener: OnItemClickListener) :
    ListAdapter<String, RecyclerView.ViewHolder>(SuggestionDiffCallback) {

    interface OnItemClickListener {
        fun onSuggestionClicked(word: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemWordSuggestionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SuggestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SuggestionViewHolder).bind(getItem(position))
    }

    inner class SuggestionViewHolder(private val binding: ItemWordSuggestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(word: String) {
            binding.apply {
                binding.tvWord.text = word

                root.setOnClickListener {
                    listener.onSuggestionClicked(word)
                }
            }
        }
    }
}

object SuggestionDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}