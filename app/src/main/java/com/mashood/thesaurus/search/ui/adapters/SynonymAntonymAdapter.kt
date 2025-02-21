package com.mashood.thesaurus.search.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mashood.thesaurus.databinding.ItemBulletedWordBinding
import kotlinx.parcelize.Parcelize

class SynonymAntonymAdapter(
    private val listener: OnClickListener
) : ListAdapter<String, RecyclerView.ViewHolder>(SingleWordDiffCallback) {

    interface OnClickListener {
        /**
         * Callback for the click on a synonym or antonym in the meanings card. On the click, we
         * will show a small tooltip for directly searching that word.
         */
        fun onSynonymAntonymClicked(word: String, clickedView: View)
    }

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
            with(binding) {
                tvWord.text = word

                layoutRoot.setOnClickListener {
                    listener.onSynonymAntonymClicked(word, layoutRoot)
                }
            }
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
