package com.mashood.thesaurus.search.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mashood.thesaurus.databinding.ItemMeaningsBinding
import com.mashood.thesaurus.search.domain.model.SearchResponse

class DefinitionsAdapter :
    ListAdapter<SearchResponse.MeaningModel.DefinitionModel, RecyclerView.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemMeaningsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderListViewHolder(binding)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as OrderListViewHolder).bind(getItem(position), position)
    }

    inner class OrderListViewHolder(private val binding: ItemMeaningsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(definitionModel: SearchResponse.MeaningModel.DefinitionModel, index: Int) {
            binding.apply {
                val position = index + 1
                tvIndex.text = position.toString()
                tvDefinition.text = definitionModel.definition

                if (definitionModel.example.isNotBlank()) {
                    tvExample.visibility = View.VISIBLE
                    tvExample.text = definitionModel.example
                }

                if (definitionModel.synonyms.isNotEmpty()) {
                    titleSynonyms.visibility = View.VISIBLE
                    tvSynonyms.visibility = View.VISIBLE
                    var synonyms = ""
                    definitionModel.synonyms.forEach {
                        synonyms = "$synonyms $it, "
                    }
                    synonyms = synonyms.dropLast(2)
                    tvSynonyms.text = synonyms
                }

                if (definitionModel.antonyms.isNotEmpty()) {
                    titleAntonyms.visibility = View.VISIBLE
                    tvAntonyms.visibility = View.VISIBLE
                    var antonyms = ""
                    definitionModel.antonyms.forEach {
                        antonyms = "$antonyms $it, "
                    }
                    antonyms = antonyms.dropLast(2)
                    tvAntonyms.text = antonyms
                }
            }
        }
    }
}

object DiffCallback : DiffUtil.ItemCallback<SearchResponse.MeaningModel.DefinitionModel>() {
    override fun areItemsTheSame(
        oldItem: SearchResponse.MeaningModel.DefinitionModel,
        newItem: SearchResponse.MeaningModel.DefinitionModel
    ): Boolean {
        return oldItem.definition == newItem.definition
    }

    override fun areContentsTheSame(
        oldItem: SearchResponse.MeaningModel.DefinitionModel,
        newItem: SearchResponse.MeaningModel.DefinitionModel
    ): Boolean {
        return oldItem == newItem
    }
}
