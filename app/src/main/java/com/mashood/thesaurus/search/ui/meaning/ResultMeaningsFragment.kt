package com.mashood.thesaurus.search.ui.meaning

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mashood.thesaurus.R
import com.mashood.thesaurus.databinding.FragmentResultMeaningsBinding

class ResultMeaningsFragment : Fragment(R.layout.fragment_result_meanings) {

    private lateinit var binding: FragmentResultMeaningsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentResultMeaningsBinding.bind(view)

        initView()
    }

    private fun initView() {

    }
}
