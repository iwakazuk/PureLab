package com.purelab.utils

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

class CustomSnackbar {
    companion object {
        fun showSnackBar(view: View, text: String) {
            val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
            val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
            params.bottomMargin = 200
            snackbar.view.layoutParams = params
            snackbar.show()
        }
    }
}

