package com.adkins.msafari.auth

import android.content.Context
import android.content.SharedPreferences
import com.adkins.msafari.models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AccountManager {
    private const val PREF_NAME = "user_accounts"
    private const val ACCOUNTS_KEY = "accounts"
    private lateinit var sharedPreferences: SharedPreferences

    private val gson = Gson()

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun checkInitialized() {
        if (!::sharedPreferences.isInitialized) {
            throw IllegalStateException("AccountManager not initialized. Call init(context) first.")
        }
    }

    fun saveAccount(user: User) {
        checkInitialized()
        val currentAccounts = getSavedAccounts().toMutableList()
        if (currentAccounts.none { it.uid == user.uid }) {
            currentAccounts.add(user)
            val json = gson.toJson(currentAccounts)
            sharedPreferences.edit().putString(ACCOUNTS_KEY, json).apply()
        }
    }

    fun getSavedAccounts(): List<User> {
        checkInitialized()
        val json = sharedPreferences.getString(ACCOUNTS_KEY, null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<User>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun clearAccounts() {
        checkInitialized()
        sharedPreferences.edit().remove(ACCOUNTS_KEY).apply()
    }
}