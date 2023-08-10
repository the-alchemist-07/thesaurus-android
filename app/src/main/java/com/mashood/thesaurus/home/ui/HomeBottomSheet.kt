package com.mashood.thesaurus.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mashood.thesaurus.databinding.MainBottomSheetContentBinding

class HomeBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: MainBottomSheetContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainBottomSheetContentBinding.inflate(inflater, container, false)

        setListeners()

        return binding.root
    }

    private fun setListeners() {

    }

    companion object {
        const val TAG = "MainBottomSheet"
    }
}