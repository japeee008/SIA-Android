package com.example.citucares.utils

import android.content.Context

object TokenManager {

    private const val PREF = "APP"
    private const val KEY = "TOKEN"

    fun save(context: Context, token: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, token)
            .apply()
    }

    fun get(context: Context): String? {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY, null)
    }
}