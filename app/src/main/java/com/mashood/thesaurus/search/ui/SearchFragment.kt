package com.mashood.thesaurus.search.ui

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.mashood.thesaurus.R
import com.mashood.thesaurus.app.common.constants.Constants.EMPTY_HISTORY
import com.mashood.thesaurus.app.common.utils.WordSuggestionsHelper
import com.mashood.thesaurus.databinding.FragmentSearchBinding
import com.mashood.thesaurus.history.domain.model.History
import com.mashood.thesaurus.history.ui.adapters.HistoryAdapter
import com.mashood.thesaurus.search.domain.model.SearchResponse
import com.mashood.thesaurus.search.ui.adapters.MeaningViewPagerAdapter
import com.mashood.thesaurus.search.ui.adapters.SuggestionAdapter
import com.mashood.thesaurus.search.ui.meaning.ResultMeaningsFragment.SearchWordListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search),
    HistoryAdapter.OnItemClickListener,
    SuggestionAdapter.OnItemClickListener,
    SearchWordListener {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModels<SearchViewModel>()
    private val historyAdapter by lazy { HistoryAdapter(this, false) }
    private val suggestionAdapter by lazy { SuggestionAdapter(this) }
    private lateinit var launcherSpeech: ActivityResultLauncher<Intent>

    private var mediaPlayer: MediaPlayer? = null
    private var audioUrl: String? = null
    private var isPlaying = false
    private var searchResultData: SearchResponse? = null
    private var isBookmarked: Boolean = false
    private var allWordsList: List<String>? = null
    private var historyList: List<History> = emptyList()
    private var showSuggestions = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        setupTransitions()
        setupRecyclerView()
        init()
        registerVoiceListener()
        setListeners()
        observeState()

        // Fetch the history list from localDB
        viewModel.getHistoriesList()
    }

    private fun setupTransitions() {
        // Customize the transitions
        sharedElementEnterTransition = ChangeBounds().apply {
            duration = 400
        }
        sharedElementReturnTransition = ChangeBounds().apply {
            duration = 200
        }
    }

    private fun init() {
        // Fetch the words from JSON file for word suggestions, and set to search view
        allWordsList = WordSuggestionsHelper.getWordsListFromJsonFile(requireContext())

        with(binding) {
            // Get word passed from bookmarks list
            val wordData = viewModel.getWordData()  // Data from bookmarks page, full word data
            val word = viewModel.getWord()          // Data from history page, just the word only

            if (wordData != null) {
                setupWordData(wordData)
            } else if (word != null) {
                searchForWord(word)
            } else {
                etSearch.requestFocus()
                showKeyboard()
            }
        }
    }

    private fun setupWordData(wordData: SearchResponse) {
        with(binding) {
            etSearch.setText(wordData.word)
            handleSearchSuccess(wordData)
            bookmarkWord()
            btnClear.visibility = View.VISIBLE
            btnVoice.visibility = View.GONE
        }
    }

    private fun searchForWord(word: String) {
        showSuggestions = false
        with(binding) {
            etSearch.setText(word)
            btnClear.visibility = View.VISIBLE
            btnVoice.visibility = View.GONE
            viewModel.searchKeyword(word)
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            recyclerHistory.adapter = historyAdapter
            recyclerSuggestion.adapter = suggestionAdapter

            historyAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    if (historyAdapter.itemCount > 0)
                        recyclerHistory.smoothScrollToPosition(0)
                }

                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    if (historyAdapter.itemCount > 0)
                        recyclerHistory.smoothScrollToPosition(0)
                }

                override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                    if (historyAdapter.itemCount > 0)
                        recyclerHistory.smoothScrollToPosition(0)
                }

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (historyAdapter.itemCount > 0)
                        recyclerHistory.smoothScrollToPosition(0)
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    if (historyAdapter.itemCount > 0)
                        recyclerHistory.smoothScrollToPosition(0)
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                    if (historyAdapter.itemCount > 0)
                        recyclerHistory.smoothScrollToPosition(0)
                }
            })
        }
    }

    private fun registerVoiceListener() {
        launcherSpeech =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val spokenText: String? =
                        it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                            .let { text -> text?.get(0) }
                    // Setting the detected word to searchbar and initiating the search.
                    binding.etSearch.setText(spokenText)
                    viewModel.checkKeyword(spokenText)
                }
            }
    }

    private fun setListeners() {
        with(binding) {
            btnClear.setOnClickListener {
                if (progressBar.isVisible) {
                    Snackbar.make(root, "Please wait, loading in progress!", Snackbar.LENGTH_SHORT).show()
                } else {
                    unBookmarkWord()
                    etSearch.setText("")
                    etSearch.requestFocus()
                    showKeyboard()
                }
            }

            btnVoice.setOnClickListener {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "hi-IN")
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("hi_IN"))
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening....")
                launcherSpeech.launch(intent)
            }

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            etSearch.doAfterTextChanged { text ->
                if (text.toString().isBlank()) {
                    btnClear.visibility = View.GONE
                    btnVoice.visibility = View.VISIBLE
                    if (historyList.isNotEmpty())
                        cardHistory.visibility = View.VISIBLE
                    // Clear and hide search suggestions
                    clearAndHideSuggestions()
                } else {
                    btnClear.visibility = View.VISIBLE
                    btnVoice.visibility = View.GONE

                    // Filter the words list by entered text, for showing suggestions
                    if (showSuggestions) {
                        val filteredList = allWordsList?.filter { word ->
                            word.startsWith(text.toString())
                        }?.take(10) ?: emptyList()
                        tvSuggestionCount.text =
                            getString(R.string.items_count_placeholder, filteredList.count())
                        suggestionAdapter.submitList(filteredList)
                        if (filteredList.isEmpty())
                            cardSuggestion.visibility = View.GONE
                        else cardSuggestion.visibility = View.VISIBLE

                        cardHistory.visibility = View.GONE
                    }
                }
                clearResultUi()
                binding.lytError.visibility = View.GONE
            }

            etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.checkKeyword(etSearch.text.toString())
                    hideKeyboard()
                    clearAndHideSuggestions()
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

            btnShare.setOnClickListener {
                showFormatTypeSelector()
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
                        is SearchState.Error -> showError(it.message)
                        is SearchState.CheckBookmarked -> handleBookmarkedWord(it.isBookmarked)
                        is SearchState.HistoryList -> handleHistoryList(it.historyList)
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
        showSuggestions = true

        searchResultData = searchResponse

        // Set data to UI
        binding.apply {
            // Set history UI as gone
            cardHistory.visibility = View.GONE
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

            // Meanings management
            // Show all the parts of speeches got in the result in different tabs
            setTabsAndMeanings(searchResponse.meanings)
        }
    }

    private fun setTabsAndMeanings(meaningsList: List<SearchResponse.MeaningModel>) {
        if (meaningsList.isNotEmpty()) {
            binding.apply {
                // Adding the contents to viewpager
                val adapter = MeaningViewPagerAdapter(this@SearchFragment, meaningsList)
                viewPager.adapter = adapter

                // Adding the tabs, with tab's name as each part of speech got in the result
                TabLayoutMediator(
                    tabPartOfSpeeches, viewPager
                ) { tab, position -> tab.text = meaningsList[position].partOfSpeech }.attach()
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
            cardMeanings.visibility = View.GONE
        }
    }

    private fun showError(errorMessage: String) {
        if (errorMessage == EMPTY_HISTORY) {
            historyList = emptyList()
            historyAdapter.submitList(historyList)
            binding.cardHistory.visibility = View.GONE
        } else {
            showLoading(false)
            binding.lytError.visibility = View.VISIBLE
        }
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

    private fun handleHistoryList(historyList: List<History>) {
        this.historyList = historyList
        historyAdapter.submitList(this.historyList)
        binding.apply {
            // Update the count
            tvHistoryCount.text = getString(R.string.items_count_placeholder, historyList.size)
            if (etSearch.text.toString().isBlank()) {
                binding.cardHistory.visibility = View.VISIBLE
            }
        }
    }

    private fun showKeyboard() {
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
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

    private fun clearAndHideSuggestions() = binding.apply {
        suggestionAdapter.submitList(emptyList())
        cardSuggestion.visibility = View.GONE
    }

    private fun showFormatTypeSelector() {
        if (searchResultData != null) {
            val bottomSheet = ShareFormatBottomSheet()
            val bundle = Bundle()
            bundle.putParcelable("wordData", searchResultData)
            bottomSheet.arguments = bundle
            bottomSheet.show(requireActivity().supportFragmentManager, ShareFormatBottomSheet.TAG)
        } else Snackbar.make(
            binding.root,
            "Failed to load the word data. Try reloading the page.",
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onHistoryWordClicked(history: History) {
        binding.apply {
            hideKeyboard()
            etSearch.setText(history.word)
            cardHistory.visibility = View.GONE
            viewModel.searchKeyword(history.word)
            recyclerHistory.smoothScrollToPosition(0)
            clearAndHideSuggestions()
        }
    }

    override fun onSuggestionClicked(selectedWord: String) {
        binding.etSearch.setText(selectedWord)
        viewModel.checkKeyword(selectedWord)
        clearAndHideSuggestions()
        hideKeyboard()
    }

    override fun onSearchTooltipClicked(word: String) {
        // Search the synonym or antonym here...
        searchForWord(word)
    }
}
