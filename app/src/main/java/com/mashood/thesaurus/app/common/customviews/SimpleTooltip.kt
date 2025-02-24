package com.mashood.thesaurus.app.common.customviews

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import com.mashood.thesaurus.R
import com.mashood.thesaurus.databinding.SimplePopupLayoutBinding

class SimpleTooltip(
    context: Context,
    message: String,
    anchorView: View,
    listener: OnClickListener
) : PopupWindow(context) {

    init {
        // Inflate the custom layout for the popup
        val inflater = LayoutInflater.from(context)
        val binding = SimplePopupLayoutBinding.inflate(inflater, null, false)

        // Setting the values for the PopupWindow
        contentView = binding.root

        // Set the message to the textView, and listener to dismiss and provide callback on click
        binding.tvDescription.text = message
        binding.root.setOnClickListener {
            listener.onTooltipClicked()
            dismiss()
        }

        // Set a custom drawable as the background of the popup
        setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_tooltip))

        isOutsideTouchable = true
        isTouchable = true

        // Get screen height
        val screenHeight = context.resources.displayMetrics.heightPixels

        // Get anchor view's location on screen
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val anchorY = location[1]

        // Measure popup height
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = contentView.measuredHeight

        // Check if there is enough space below
        if (screenHeight - (anchorY + anchorView.height) >= popupHeight) {
            // Show below the anchor view (default)
            showAsDropDown(anchorView, 0, 0)
        } else {
            // If there is no enough space below, show above the anchor view
            showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], anchorY - popupHeight)
        }
    }

    interface OnClickListener {
        fun onTooltipClicked()
    }
}
