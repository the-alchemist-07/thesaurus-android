package com.mashood.thesaurus.search.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mashood.thesaurus.databinding.BottomSheetShareFormatBinding
import com.mashood.thesaurus.search.domain.model.SearchResponse

class ShareFormatBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetShareFormatBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetShareFormatBinding.inflate(inflater, container, false)

        setListeners()
        return binding.root
    }

    private fun setListeners() = binding.apply {
        lytPlainText.setOnClickListener {
            dismiss()
            shareResults(PLAIN_TEXT)
        }

        lytFormattedText.setOnClickListener {
            dismiss()
            shareResults(FORMATTED_TEXT)
        }
    }

    private fun shareResults(formatType: String) {
        val wordData = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                arguments?.getParcelable("wordData", SearchResponse::class.java)

            else ->
                @Suppress("DEPRECATION") arguments?.getParcelable("wordData")
        }

        wordData?.let { data ->
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            when (formatType) {
                PLAIN_TEXT ->
                    sendIntent.putExtra(Intent.EXTRA_TEXT, data.toNormalText())

                FORMATTED_TEXT ->
                    sendIntent.putExtra(Intent.EXTRA_TEXT, data.toWhatsappFormattedText())
            }
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
    }

    companion object {
        const val PLAIN_TEXT = "plain"
        const val FORMATTED_TEXT = "formatted"
        const val TAG = "ShareFormatBottomSheet"
    }
}
