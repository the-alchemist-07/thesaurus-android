package com.mashood.thesaurus.app.common.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

/**
 * Keeps important content clear of system bars while allowing the window to draw edge-to-edge.
 * The original padding is captured once so repeated inset dispatches remain idempotent.
 */
fun View.applySystemBarInsets(
    includeTop: Boolean = true,
    includeBottom: Boolean = true,
    includeIme: Boolean = false
) {
    val initialPaddingLeft = paddingLeft
    val initialPaddingTop = paddingTop
    val initialPaddingRight = paddingRight
    val initialPaddingBottom = paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insetTypes = WindowInsetsCompat.Type.systemBars() or
            WindowInsetsCompat.Type.displayCutout() or
            if (includeIme) WindowInsetsCompat.Type.ime() else 0
        val insets = windowInsets.getInsets(insetTypes)

        view.updatePadding(
            left = initialPaddingLeft + insets.left,
            top = initialPaddingTop + if (includeTop) insets.top else 0,
            right = initialPaddingRight + insets.right,
            bottom = initialPaddingBottom + if (includeBottom) insets.bottom else 0
        )
        windowInsets
    }

    ViewCompat.requestApplyInsets(this)
}

/**
 * Adds the bottom system-bar/IME inset to an existing bottom margin, such as a bottom action.
 */
fun View.applyBottomInsetMargin(includeIme: Boolean = false) {
    val initialBottomMargin = (layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin
        ?: return

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insetTypes = WindowInsetsCompat.Type.systemBars() or
            WindowInsetsCompat.Type.displayCutout() or
            if (includeIme) WindowInsetsCompat.Type.ime() else 0
        val insets = windowInsets.getInsets(insetTypes)

        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = initialBottomMargin + insets.bottom
        }
        windowInsets
    }

    ViewCompat.requestApplyInsets(this)
}
