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
import android.widget.ArrayAdapter
import android.widget.TextView
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
import androidx.transition.ChangeBounds
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.mashood.thesaurus.R
import com.mashood.thesaurus.app.common.Constants.EMPTY_HISTORY
import com.mashood.thesaurus.app.common.WordSuggestionsHelper
import com.mashood.thesaurus.databinding.FragmentSearchBinding
import com.mashood.thesaurus.history.domain.model.History
import com.mashood.thesaurus.history.ui.adapters.HistoryAdapter
import com.mashood.thesaurus.search.domain.model.SearchResponse
import com.mashood.thesaurus.search.ui.adapters.MeaningViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search),
    HistoryAdapter.OnItemClickListener{

    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModels<SearchViewModel>()
    private val historyAdapter by lazy { HistoryAdapter(this, false) }
    private lateinit var launcherSpeech: ActivityResultLauncher<Intent>

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
            duration = 400
        }
        sharedElementReturnTransition = ChangeBounds().apply {
            duration = 200
        }

        init()
        setupRecyclerView()
        registerVoiceListener()
        setListeners()
        observeState()
    }

    private fun init() {
        with(binding) {
            // Get word passed from bookmarks list
            val wordData = viewModel.getWordData()  // Data from bookmarks page, full word data
            val word = viewModel.getWord()          // Data from history page, just the word only
            if (wordData != null) {
                etSearch.setText(wordData.word)
                handleSearchSuccess(wordData)
                bookmarkWord()
                btnClear.visibility = View.VISIBLE
                btnVoice.visibility = View.GONE
            } else if (word != null) {
                etSearch.setText(word)
                btnClear.visibility = View.VISIBLE
                btnVoice.visibility = View.GONE
                viewModel.searchKeyword(word)
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

    private fun setupRecyclerView() {
        binding.apply {
            recyclerHistory.adapter = historyAdapter
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
                    Snackbar.make(root, "Loading in progress!", Snackbar.LENGTH_SHORT).show()
                } else {
                    unBookmarkWord()
                    etSearch.setText("")
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
                    cardHistory.visibility = View.VISIBLE
                } else {
                    btnClear.visibility = View.VISIBLE
                    btnVoice.visibility = View.GONE
                    cardHistory.visibility = View.GONE
                }
                clearResultUi()
                binding.lytError.visibility = View.GONE
            }

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

            // Meanings management
            // Show all the parts of speeches got in the result in different tabs
            setTabsAndMeanings(searchResponse.meanings)
        }
    }

    private fun setTabsAndMeanings(meaningsList: List<SearchResponse.MeaningModel>) {
        if (meaningsList.isNotEmpty()) {
            binding.apply {
                // Adding the contents to viewpager
                val adapter = MeaningViewPagerAdapter(requireActivity(), meaningsList)
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
            historyAdapter.submitList(emptyList())
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
        binding.cardHistory.visibility = View.VISIBLE
        historyAdapter.submitList(historyList)
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

    override fun onHistoryWordClicked(history: History) {
        binding.apply {
            etSearch.setText(history.word)
            cardHistory.visibility = View.GONE
            viewModel.searchKeyword(history.word)
        }
    }

    override fun onHistoryWordRemoveClicked(history: History) {
        // Nothing to do here...
    }
}
