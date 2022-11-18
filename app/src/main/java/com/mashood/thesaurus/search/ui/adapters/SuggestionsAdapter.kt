package com.mashood.thesaurus.search.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mashood.thesaurus.databinding.ItemSuggestionsBinding

class SuggestionsAdapter(private val listener: OnItemClickListener) :
    ListAdapter<String, RecyclerView.ViewHolder>(SuggestionsDiffCallback) {

    interface OnItemClickListener {
        fun onItemClicked(selectedWord: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemSuggestionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BookmarkViewHolder).bind(getItem(position))
    }

    inner class BookmarkViewHolder(private val binding: ItemSuggestionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(word: String) {
            binding.apply {
                tvWord.text = word
                tvFirstLetter.text = word[0].toString().uppercase()

                root.setOnClickListener {
                    listener.onItemClicked(word)
                }
            }
        }
    }
}

object SuggestionsDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}
