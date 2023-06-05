package com.mashood.thesaurus.search.ui

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.mashood.thesaurus.R
import com.mashood.thesaurus.app.common.WordSuggestionsHelper
import com.mashood.thesaurus.databinding.FragmentSearchBinding
import com.mashood.thesaurus.search.domain.model.SearchResponse
import com.mashood.thesaurus.search.ui.adapters.DefinitionsAdapter
import com.mashood.thesaurus.search.ui.adapters.SuggestionsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search), SuggestionsAdapter.OnItemClickListener {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModels<SearchViewModel>()
    private val definitionsAdapter: DefinitionsAdapter by lazy { DefinitionsAdapter() }

    private var mediaPlayer: MediaPlayer? = null
    private var audioUrl: String? = null
    private var isPlaying = false
    private var searchResultData: SearchResponse? = null
    private var isBookmarked: Boolean = false
    private var wordsList: List<String>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        // Customize the transitions
        sharedElementEnterTransition = ChangeBounds().apply {
            duration = 750
        }
        sharedElementReturnTransition = ChangeBounds().apply {
            duration = 300
        }

        init()
        setupRecyclerView()
        setListeners()
        observeState()
    }

    private fun init() {
        with(binding) {
            // Get word passed from bookmarks list
            val wordData = viewModel.getWordData()
            if (wordData != null) {
                etSearch.setText(wordData.word)
                handleSearchSuccess(wordData)
                bookmarkWord()
                btnClear.visibility = View.VISIBLE
            } else {
                etSearch.requestFocus()
                showKeyboard()
            }

            // Fetch the words from JSON file for word suggestions, and set to search view
            wordsList = WordSuggestionsHelper.getWordsListFromJsonFile(requireContext())
            if (wordsList != null) {
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    wordsList!!.toMutableList()
                ).also { adapter ->
                    etSearch.setAdapter(adapter)
                }
            }
        }
    }

    private fun showKeyboard() {
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupRecyclerView() {
        binding.recyclerDefinitions.adapter = definitionsAdapter
//        binding.recyclerSuggestions.adapter = suggestionsAdapter
    }

    private fun setListeners() {
        with(binding) {
            btnClear.setOnClickListener {
                if (progressBar.isVisible) {
                    Snackbar.make(root, "Loading in progress!", Snackbar.LENGTH_SHORT).show()
                } else {
                    unBookmarkWord()
                    etSearch.setText("")
                }
            }

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            etSearch.doAfterTextChanged { text ->
                if (text.toString().isBlank())
                    btnClear.visibility = View.INVISIBLE
                else
                    btnClear.visibility = View.VISIBLE
                clearResultUi()
                binding.lytError.visibility = View.GONE
            }

            /*etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    if (p0.toString().isBlank()) {
                        btnClear.visibility = View.INVISIBLE
                    }
                    else {
                        btnClear.visibility = View.VISIBLE
                    }
                    clearResultUi()
                    binding.lytError.visibility = View.GONE
                }
            })*/

            etSearch.setOnItemClickListener { parent, _, position, _ ->
                val selectedWord = parent.getItemAtPosition(position) as String
                viewModel.checkKeyword(selectedWord)
                hideKeyboard()
            }

            etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.checkKeyword(etSearch.text.toString())
                    hideKeyboard()
                    return@OnEditorActionListener true
                }
                false
            })

            btnPlayAudio.setOnClickListener {
                if (!audioUrl.isNullOrBlank()) {
                    isPlaying = true
                    mediaPlayer = MediaPlayer()
                    try {
                        mediaPlayer?.let { mediaPlayer ->
                            mediaPlayer.setDataSource(audioUrl)
                            mediaPlayer.prepare()
                            mediaPlayer.start()
                        }
                    } catch (e: IOException) {
                        Log.e("Exception", "MediaPlayer prepare() failed")
                    }
                } else
                    Snackbar.make(binding.root, "No pronunciation found", Snackbar.LENGTH_SHORT)
                        .show()
            }

            tabPartOfSpeeches.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    titleSynonyms.visibility = View.GONE
                    listSynonyms.visibility = View.GONE
                    titleAntonyms.visibility = View.GONE
                    listAntonyms.visibility = View.GONE

                    if (searchResultData != null) {
                        val selectedTabPosition = tabPartOfSpeeches.selectedTabPosition
                        val definitionsList =
                            searchResultData!!.meanings[selectedTabPosition].definitions
                        definitionsAdapter.submitList(definitionsList)

                        // Set synonyms
                        val synonymsList = searchResultData!!.meanings[selectedTabPosition].synonyms
                        if (synonymsList.isNotEmpty()) {
                            // Add bullets to the list
                            val bulletedSynonymsList: MutableList<String> = mutableListOf()
                            synonymsList.forEach { synonym ->
                                bulletedSynonymsList.add("➼ $synonym")
                            }
                            // Set visibility of UI
                            titleSynonyms.visibility = View.VISIBLE
                            listSynonyms.visibility = View.VISIBLE
                            // Create adapter and set to listView
                            val arrayAdapter = ArrayAdapter(
                                requireContext(),
                                R.layout.item_single_list, bulletedSynonymsList
                            )
                            listSynonyms.adapter = arrayAdapter
                            setListViewHeightBasedOnChildren(listSynonyms)
                        }

                        // Set antonyms
                        val antonymsList = searchResultData!!.meanings[selectedTabPosition].antonyms
                        if (antonymsList.isNotEmpty()) {
                            // Add bullets to the list
                            val bulletedAntonymsList: MutableList<String> = mutableListOf()
                            antonymsList.forEach { antonym ->
                                bulletedAntonymsList.add("➼ $antonym")
                            }
                            // Set visibility of UI
                            titleAntonyms.visibility = View.VISIBLE
                            listAntonyms.visibility = View.VISIBLE
                            // Create adapter and set to listView
                            val arrayAdapter = ArrayAdapter(
                                requireContext(),
                                R.layout.item_single_list, bulletedAntonymsList
                            )
                            listAntonyms.adapter = arrayAdapter
                            setListViewHeightBasedOnChildren(listAntonyms)
                        }
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {}
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
            })

            btnBookmark.setOnClickListener {
                if (!isBookmarked) {
                    bookmarkWord()
                    if (searchResultData != null) {
                        viewModel.addToBookmarks(searchResultData!!)
                    }
                } else {
                    unBookmarkWord()
                    if (searchResultData != null) {
                        viewModel.removeFromBookmarks(searchResultData!!)
                    }
                }
            }
        }
    }


    private fun bookmarkWord() {
        binding.apply {
            btnBookmark.speed = 3f
            isBookmarked = true
            btnBookmark.setMinFrame(0)
            btnBookmark.setMaxFrame(50)
            btnBookmark.playAnimation()
        }
    }

    private fun unBookmarkWord() {
        binding.apply {
            btnBookmark.speed = 1F
            isBookmarked = false
            btnBookmark.setMinFrame(50)
            btnBookmark.setMaxFrame(74)
            btnBookmark.playAnimation()
        }
    }


    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchState.collect {
                    when (it) {
                        is SearchState.Loading -> showLoading(true)
                        is SearchState.SearchSuccess -> handleSearchSuccess(it.searchResponse)
                        is SearchState.Error -> showError()
                        is SearchState.CheckBookmarked -> handleBookmarkedWord(it.isBookmarked)
                        is SearchState.Idle -> Unit
                    }
                }
            }
        }
    }


    private fun showLoading(flag: Boolean) {
        with(binding) {
            if (flag) {
                progressBar.visibility = View.VISIBLE
                binding.etSearch.clearFocus()
            } else
                progressBar.visibility = View.INVISIBLE
        }
    }


    private fun handleSearchSuccess(searchResponse: SearchResponse) {
        showLoading(false)
        searchResultData = searchResponse

        // Set data to UI
        binding.apply {
            // Set result UI as visible
            cardResult.visibility = View.VISIBLE
            cardMeanings.visibility = View.VISIBLE

            tvWord.text = searchResponse.word

            // Check phonetics list
            searchResponse.phonetics.forEach { phonetic ->
                if (phonetic.audio.isNotBlank() && phonetic.text.isNotBlank()) {
                    tvPronunciation.text = phonetic.text
                    audioUrl = phonetic.audio
                }
            }

            // Card meanings management
            tabPartOfSpeeches.removeAllTabs()
            if (searchResponse.meanings.isNotEmpty()) {
                val meaningsList = searchResponse.meanings
                // Set tabs (parts of speech)
                meaningsList.forEach { meaning ->
                    tabPartOfSpeeches.addTab(
                        tabPartOfSpeeches.newTab().setText(meaning.partOfSpeech)
                    )
                }

                // Set definitions for the first tab by default
                val selectedTabPosition = tabPartOfSpeeches.selectedTabPosition
                val definitionsList = meaningsList[selectedTabPosition].definitions
                definitionsAdapter.submitList(definitionsList)

                // Set synonyms for the first tab by default
                if (meaningsList[selectedTabPosition].synonyms.isNotEmpty()) {
                    // Add bullets to the list
                    val synonymsList: MutableList<String> = mutableListOf()
                    meaningsList[selectedTabPosition].synonyms.forEach { synonym ->
                        synonymsList.add("➼ $synonym")
                    }
                    // Set visibility of UI
                    titleSynonyms.visibility = View.VISIBLE
                    listSynonyms.visibility = View.VISIBLE
                    // Create adapter and set to listView
                    val arrayAdapter = ArrayAdapter(
                        requireContext(),
                        R.layout.item_single_list,
                        synonymsList
                    )
                    listSynonyms.adapter = arrayAdapter
                    setListViewHeightBasedOnChildren(listSynonyms)
                }

                // Set antonyms for the first tab by default
                if (meaningsList[selectedTabPosition].antonyms.isNotEmpty()) {
                    // Add bullets to the list
                    val antonymsList: MutableList<String> = mutableListOf()
                    meaningsList[selectedTabPosition].antonyms.forEach { antonym ->
                        antonymsList.add("➼ $antonym")
                    }
                    // Set visibility of UI
                    titleAntonyms.visibility = View.VISIBLE
                    listAntonyms.visibility = View.VISIBLE
                    // Create adapter and set to listView
                    val arrayAdapter = ArrayAdapter(
                        requireContext(),
                        R.layout.item_single_list,
                        antonymsList
                    )
                    listAntonyms.adapter = arrayAdapter
                    setListViewHeightBasedOnChildren(listAntonyms)
                }
            }
        }
    }

    private fun clearResultUi() {
        binding.apply {
            // Clear result card
            tvWord.text = ""
            audioUrl = null
            tvPronunciation.text = ""
            cardResult.visibility = View.GONE

            // Clear meanings card
            tabPartOfSpeeches.removeAllTabs()
            definitionsAdapter.submitList(emptyList())
            cardMeanings.visibility = View.GONE
        }
    }

    private fun showError() {
        showLoading(false)
        binding.lytError.visibility = View.VISIBLE
    }

    private fun handleBookmarkedWord(isBookmarked: Boolean) {
        if (isBookmarked) {
            binding.apply {
                btnBookmark.speed = 3f
                this@SearchFragment.isBookmarked = true
                btnBookmark.setMinFrame(0)
                btnBookmark.setMaxFrame(50)
                btnBookmark.playAnimation()
            }
        }
    }

    private fun hideKeyboard() {
        try {
            val inputMethodManager =
                requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter: ListAdapter = listView.adapter ?: return
        var totalHeight = 0
        val desiredWidth =
            View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.AT_MOST)
        for (i in 0 until listAdapter.count) {
            val listItem: View = listAdapter.getView(i, null, listView)
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
            totalHeight += listItem.measuredHeight
        }
        val params: ViewGroup.LayoutParams = listView.layoutParams
//        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
        params.height = totalHeight
        listView.layoutParams = params
        listView.requestLayout()
    }

    override fun onItemClicked(selectedWord: String) {
        binding.etSearch.setText(selectedWord)
        viewModel.checkKeyword(selectedWord)
        hideKeyboard()
    }
}