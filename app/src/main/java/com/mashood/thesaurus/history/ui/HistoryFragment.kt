package com.mashood.thesaurus.history.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.mashood.thesaurus.R
import com.mashood.thesaurus.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var binding: FragmentHistoryBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHistoryBinding.bind(view)
    }
}
