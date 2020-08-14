package com.example.contactbook.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat



fun hideKeyboard(view: View, context: Context) {
    val inputMethodManager = ContextCompat.getSystemService(context, InputMethodManager::class.java)!!
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}