package com.mashood.thesaurus.history.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mashood.thesaurus.app.common.capitalizeFirstLetter
import com.mashood.thesaurus.databinding.ItemHistoryBinding
import com.mashood.thesaurus.history.domain.model.History

class HistoryAdapter(
    private val listener: OnItemClickListener,
    private val showDeleteButton: Boolean = true
) : ListAdapter<History, RecyclerView.ViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onHistoryWordClicked(history: History)
        fun onHistoryWordRemoveClicked(history: History)
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

        fun bind(history: History) {
            binding.apply {
                tvWord.text = history.word
                tvFirstLetter.text = history.word[0].toString().uppercase()

                if (!showDeleteButton)
                    btnRemove.visibility = View.GONE

                btnRemove.setOnClickListener {
                    listener.onHistoryWordRemoveClicked(history)
                }

                root.setOnClickListener {
                    listener.onHistoryWordClicked(history)
                }
            }
        }
    }
}

object DiffCallback : DiffUtil.ItemCallback<History>() {
    override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem == newItem
    }
}