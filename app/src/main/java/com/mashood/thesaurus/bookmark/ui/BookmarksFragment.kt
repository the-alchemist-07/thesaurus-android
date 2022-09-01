package com.mashood.thesaurus.bookmark.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mashood.thesaurus.R
import com.mashood.thesaurus.databinding.FragmentBookmarksBinding

class BookmarksFragment : Fragment(R.layout.fragment_bookmarks) {

    private lateinit var binding: FragmentBookmarksBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBookmarksBinding.bind(view)


    }

}