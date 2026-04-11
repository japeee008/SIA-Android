package com.example.citucares.core.utils

import android.content.Context

object TokenManager {

    private const val PREF = "APP"
    private const val KEY = "USER"

    fun save(context: Context, value: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, value)
            .apply()
    }

    fun get(context: Context): String? {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY, null)
    }
}