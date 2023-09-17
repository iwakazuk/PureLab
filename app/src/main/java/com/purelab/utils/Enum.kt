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

fun <T : Enum<T>> getEnumIndex(value: T?, enumValues: Array<T>): Int {
    return enumValues.indexOf(value)
}

fun <T : Enum<T>> getEnumIndex(
    value: String?,
    enumValues: Array<T>,
    context: Context
): Int {
    return enumValues.indexOf(
        enumValues.firstOrNull  {
            it.toEnumString(context) == value
        } ?: enumValues[0]
    )
}
