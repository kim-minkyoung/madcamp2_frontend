package com.example.madcamp2_frontend.view.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(context: Context) {

    companion object {
        private const val PREFS_NAME = "MyAppPreferences"
        private const val KEY_USER_ID = "_id"
        private const val KEY_WORD_ID = "_word"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserId(userid: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userid).apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    fun clearUserId() {
        sharedPreferences.edit().remove(KEY_USER_ID).apply()
    }

    fun saveCurrentWord(word: String) {
        sharedPreferences.edit().putString(KEY_WORD_ID, word).apply()
    }

    fun getCurrentWord(): String? {
        return sharedPreferences.getString(KEY_WORD_ID, null)
    }

    fun clearCurrentWord() {
        sharedPreferences.edit().remove(KEY_WORD_ID).apply()
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
