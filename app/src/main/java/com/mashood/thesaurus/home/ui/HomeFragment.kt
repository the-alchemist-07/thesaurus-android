package com.mashood.thesaurus.home.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.mashood.thesaurus.R
import com.mashood.thesaurus.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        checkShortcutIntentAction()
        setListeners()
    }

    private fun checkShortcutIntentAction() {
        if ("android.intent.action.SEARCH" == requireActivity().intent.action) {
            navigateToSearchScreen()
        }
    }

    private fun setListeners() {
        binding.apply {
            cardSearch.setOnClickListener {
                navigateToSearchScreen()
            }

            btnMoreOptions.setOnClickListener {
                val bottomSheet = HomeMenuBottomSheet()
                bottomSheet.show(requireActivity().supportFragmentManager, HomeMenuBottomSheet.TAG)
            }
        }
    }

    private fun navigateToSearchScreen() {
        val direction = HomeFragmentDirections.actionHomeFragmentToSearchFragment(
            wordData = null,
            word = null
        )
        val extras = FragmentNavigatorExtras(
            binding.cardSearch to binding.cardSearch.transitionName
        )
        findNavController().navigate(direction, extras)
    }
}