package com.mashood.thesaurus.search.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mashood.thesaurus.databinding.ItemBulletedWordBinding

class SynonymAntonymAdapter :
    ListAdapter<String, RecyclerView.ViewHolder>(SingleWordDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemBulletedWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SingleWordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SingleWordViewHolder).bind(getItem(position))
    }

    inner class SingleWordViewHolder(private val binding: ItemBulletedWordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(word: String) {
            binding.tvWord.text = word
        }
    }
}

object SingleWordDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(
        oldItem: String,
        newItem: String
    ): Boolean {
        return false
    }

    override fun areContentsTheSame(
        oldItem: String,
        newItem: String
    ): Boolean {
        return oldItem == newItem
    }
}
