package com.purelab.utils

import android.content.Context

val Enum<*>.typeName: String
    get() {
        return this.javaClass.simpleName
    }

fun Enum<*>.toEnumString(context: Context, suffix: String? = null): String {
    var key = "utils.Enum." + this.typeName + "." + this.name
    suffix?.let {
        key += ".$it"
    }

    val resources = context.resources
    val resId = resources.getIdentifier(key, "string", context.packageName)

    return if (resId == 0) {
        key
    } else {
        resources.getString(resId)
    }
}
