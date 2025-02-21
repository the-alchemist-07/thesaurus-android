package com.mashood.thesaurus.search.ui.meaning

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.mashood.thesaurus.R
import com.mashood.thesaurus.app.common.customviews.SimpleTooltip
import com.mashood.thesaurus.databinding.FragmentResultMeaningsBinding
import com.mashood.thesaurus.search.domain.model.SearchResponse
import com.mashood.thesaurus.search.ui.adapters.DefinitionsAdapter
import com.mashood.thesaurus.search.ui.adapters.SynonymAntonymAdapter
import dagger.hilt.android.AndroidEntryPoint

//@AndroidEntryPoint
class ResultMeaningsFragment : Fragment(R.layout.fragment_result_meanings),
    SynonymAntonymAdapter.OnClickListener {

    private lateinit var binding: FragmentResultMeaningsBinding
    private val definitionsAdapter: DefinitionsAdapter by lazy { DefinitionsAdapter() }
    private val synonymsAdapter: SynonymAntonymAdapter by lazy { SynonymAntonymAdapter(this) }
    private val antonymsAdapter: SynonymAntonymAdapter by lazy { SynonymAntonymAdapter(this) }
    private var wordMeaning: SearchResponse.MeaningModel? = null
    private var listener: SearchWordListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fetch the word meanings from the arguments
        wordMeaning = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                arguments?.getParcelable(ARG_WORD_MEANING, SearchResponse.MeaningModel::class.java)

            else ->
                @Suppress("DEPRECATION") arguments?.getParcelable(ARG_WORD_MEANING)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentResultMeaningsBinding.bind(view)

        initRecyclerViews()
        initView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // If parent fragment (SearchFragment) has implemented SearchWordListener, we will
        // initialize the listener object from our parent fragment.
        if (parentFragment is SearchWordListener)
            listener = parentFragment as SearchWordListener
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
            wordMeaning?.let { meaning ->
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

    interface SearchWordListener {
        /**
         * Callback from ResultMeaningsFragment to the parent fragment (SearchFragment) on clicking
         * the tooltip (which is shown on clicking a synonym or antonym). The synonym or antonym
         * which is shown in the tooltip will be passed in the parameter 'word'.
         */
        fun onSearchTooltipClicked(word: String)
    }

    override fun onSynonymAntonymClicked(word: String, clickedView: View) {
        Log.d("TEST111", "Selected word: $word")
        // We will show a small tooltip here for the confirmation from the user to search that word
        SimpleTooltip(
            requireContext(),
            "Search '$word'",
            object : SimpleTooltip.OnClickListener {
                override fun onTooltipClicked() {
                    listener?.onSearchTooltipClicked(word)
                }
            }
        ).apply {
            isOutsideTouchable = true
            isTouchable = true
            showAsDropDown(clickedView, -10, 10)
        }
    }

    companion object {
        private const val ARG_WORD_MEANING = "arg_word_data"

        fun newInstance(meaning: SearchResponse.MeaningModel): ResultMeaningsFragment {
            val fragment = ResultMeaningsFragment()
            val bundle = Bundle().apply {
                putParcelable(ARG_WORD_MEANING, meaning)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}
