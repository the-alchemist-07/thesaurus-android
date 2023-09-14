package com.mashood.thesaurus.history.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mashood.thesaurus.databinding.ItemHistoryBinding

class HistoryAdapter(private val listener: OnItemClickListener) :
    ListAdapter<String, RecyclerView.ViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onWordClicked(word: String)
        fun onWordRemoveClicked(word: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HistoryViewHolder).bind(getItem(position))
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(word: String) {
            binding.apply {
                tvWord.text = word
                tvFirstLetter.text = word[0].toString().uppercase()

                btnRemove.setOnClickListener {
                    listener.onWordRemoveClicked(word)
                }

                root.setOnClickListener {
                    listener.onWordClicked(word)
                }
            }
        }
    }
}

object DiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}