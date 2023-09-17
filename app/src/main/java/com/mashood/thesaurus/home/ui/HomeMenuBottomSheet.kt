package com.mashood.thesaurus.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.mashood.thesaurus.databinding.HomeBottomSheetContentBinding

class HomeMenuBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: HomeBottomSheetContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBottomSheetContentBinding.inflate(inflater, container, false)

        setListeners()
        return binding.root
    }

    private fun setListeners() {
        binding.apply {
            lytBookmarks.setOnClickListener {
                dismiss()
                val direction = HomeFragmentDirections.actionHomeFragmentToBookmarksFragment()
                val extras = FragmentNavigatorExtras(
                    lytBookmarks to lytBookmarks.transitionName
                )
                findNavController().navigate(direction, extras)
            }

            lytHistory.setOnClickListener {
                dismiss()
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToHistoryFragment()
                )
            }

            lytAbout.setOnClickListener {
                dismiss()
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToAboutDialog()
                )
            }
        }
    }

    companion object {
        const val TAG = "MainBottomSheet"
    }
}
