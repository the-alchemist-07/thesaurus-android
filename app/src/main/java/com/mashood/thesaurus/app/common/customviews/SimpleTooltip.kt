package com.mashood.thesaurus.app.common.customviews

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import com.mashood.thesaurus.R
import com.mashood.thesaurus.app.common.utils.getScreenWidth
import com.mashood.thesaurus.databinding.SimplePopupLayoutBinding

class SimpleTooltip(
    context: Context,
    message: String,
    listener: OnClickListener
) : PopupWindow(context) {

    init {
        // Inflate the custom layout for the popup
        val inflater = LayoutInflater.from(context)
        val binding = SimplePopupLayoutBinding.inflate(inflater, null, false)

        // Setting the values for the PopupWindow
        contentView = binding.root
        width = getDp(context, (context.getScreenWidth() / 2.42f))

        // Set the message to the textView, and listener to dismiss on click
        binding.tvDescription.text = message
        binding.root.setOnClickListener { listener.onTooltipClicked() }

        // Set a custom drawable as the background of the popup
        setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_tooltip))
    }

    private fun getDp(context: Context, value: Float): Int {
        val dm = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm).toInt()
    }

    interface OnClickListener {
        fun onTooltipClicked()
    }
}