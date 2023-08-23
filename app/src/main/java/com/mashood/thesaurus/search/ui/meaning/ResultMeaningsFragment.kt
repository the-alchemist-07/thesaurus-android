package com.mashood.thesaurus.search.ui.meaning

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mashood.thesaurus.R
import com.mashood.thesaurus.databinding.FragmentResultMeaningsBinding
import com.mashood.thesaurus.search.domain.model.SearchResponse
import com.mashood.thesaurus.search.ui.adapters.DefinitionsAdapter
import com.mashood.thesaurus.search.ui.adapters.SynonymAntonymAdapter

class ResultMeaningsFragment(
    private val meaning: SearchResponse.MeaningModel
) : Fragment(R.layout.fragment_result_meanings) {

    private lateinit var binding: FragmentResultMeaningsBinding
    private val definitionsAdapter: DefinitionsAdapter by lazy { DefinitionsAdapter() }
    private val synonymsAdapter: SynonymAntonymAdapter by lazy { SynonymAntonymAdapter() }
    private val antonymsAdapter: SynonymAntonymAdapter by lazy { SynonymAntonymAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentResultMeaningsBinding.bind(view)

        initRecyclerViews()
        initView()
    }

    private fun initRecyclerViews() {
        binding.apply {
            recyclerDefinitions.adapter = definitionsAdapter
            recyclerSynonyms.adapter = synonymsAdapter
            recyclerAntonyms.adapter = antonymsAdapter
        }
    }

    private fun initView() {
        binding.apply {
            // Set definitions for the first tab by default
            val definitionsList = meaning.definitions
            definitionsAdapter.submitList(definitionsList)

            // Set synonyms for the first tab by default
            if (meaning.synonyms.isNotEmpty()) {
                // Set visibility of UI
                titleSynonyms.visibility = View.VISIBLE
                recyclerSynonyms.visibility = View.VISIBLE
                // Submit the list
                synonymsAdapter.submitList(meaning.synonyms)
            }

            // Set antonyms for the first tab by default
            if (meaning.antonyms.isNotEmpty()) {
                // Set visibility of UI
                titleAntonyms.visibility = View.VISIBLE
                recyclerAntonyms.visibility = View.VISIBLE
                // Submit the list
                antonymsAdapter.submitList(meaning.antonyms)
            }
        }
    }
}
