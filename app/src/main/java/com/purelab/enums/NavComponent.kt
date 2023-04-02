package com.purelab.enums

import androidx.annotation.StringRes
import com.purelab.R

enum class NavComponent(
    @StringRes val titleRes: Int
) {
    BOTTOM_NAVIGATION(
        R.string.fragment_item_list_title
    ),
}