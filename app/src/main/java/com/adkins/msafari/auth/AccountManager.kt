package com.adkins.msafari.auth

import android.content.Context
import android.content.SharedPreferences
import com.adkins.msafari.models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AccountManager {
    private const val PREF_NAME = "user_accounts"
    private const val ACCOUNTS_KEY = "accounts"
    private const val ACTIVE_ACCOUNT_KEY = "active_account"
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
            sharedPreferences.edit().putString(ACCOUNTS_KEY, gson.toJson(currentAccounts)).apply()
        }
        setCurrentAccount(user) // ✅ automatically sets as active
    }

    fun getSavedAccounts(): List<User> {
        checkInitialized()
        val json = sharedPreferences.getString(ACCOUNTS_KEY, null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<User>>() {}.type
            gson.fromJson(json, type)
        } else emptyList()
    }

    fun setCurrentAccount(user: User) {
        checkInitialized()
        sharedPreferences.edit().putString(ACTIVE_ACCOUNT_KEY, gson.toJson(user)).apply()
    }

    fun getCurrentAccount(): User? {
        checkInitialized()
        val json = sharedPreferences.getString(ACTIVE_ACCOUNT_KEY, null)
        return json?.let { gson.fromJson(it, User::class.java) }
    }

    fun removeAccount(uid: String) {
        checkInitialized()
        val updated = getSavedAccounts().filterNot { it.uid == uid }
        sharedPreferences.edit().putString(ACCOUNTS_KEY, gson.toJson(updated)).apply()

        // remove active account if it was deleted
        val current = getCurrentAccount()
        if (current?.uid == uid) {
            sharedPreferences.edit().remove(ACTIVE_ACCOUNT_KEY).apply()
        }
    }

    fun clearAccounts() {
        checkInitialized()
        sharedPreferences.edit()
            .remove(ACCOUNTS_KEY)
            .remove(ACTIVE_ACCOUNT_KEY)
            .apply()
    }
}