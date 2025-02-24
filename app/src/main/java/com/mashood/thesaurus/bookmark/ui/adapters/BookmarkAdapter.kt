package com.mashood.thesaurus.bookmark.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mashood.thesaurus.app.common.utils.capitalizeFirstLetter
import com.mashood.thesaurus.databinding.ItemBookmarkBinding
import com.mashood.thesaurus.search.domain.model.SearchResponse

class BookmarkAdapter(private val listener: OnItemClickListener) :
    ListAdapter<SearchResponse, RecyclerView.ViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onItemClicked(data: SearchResponse)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BookmarkViewHolder).bind(getItem(position))
    }

    inner class BookmarkViewHolder(private val binding: ItemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SearchResponse) {
            binding.apply {
                tvWord.text = data.word.capitalizeFirstLetter()
                tvFirstLetter.text = data.word[0].toString().uppercase()

                var isPronunciationSet = false
                data.phonetics.forEach { phonetic ->
                    if (phonetic.audio.isNotBlank() && phonetic.text.isNotBlank()) {
                        tvPronunciation.text = phonetic.text
                        isPronunciationSet = true
                    }
                }
                // If pronunciation is not available, hide the textView for it
                if (!isPronunciationSet)
                    tvPronunciation.visibility = View.GONE

                root.setOnClickListener {
                    listener.onItemClicked(data)
                }
            }
        }
    }
}

object DiffCallback : DiffUtil.ItemCallback<SearchResponse>() {
    override fun areItemsTheSame(oldItem: SearchResponse, newItem: SearchResponse): Boolean {
        return oldItem.word == newItem.word
    }

    override fun areContentsTheSame(oldItem: SearchResponse, newItem: SearchResponse): Boolean {
        return oldItem == newItem
    }
}
