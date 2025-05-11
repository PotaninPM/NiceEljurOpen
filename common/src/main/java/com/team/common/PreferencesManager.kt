package com.team.common

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAuthToken(): String {
        return prefs.getString(KEY_AUTH_TOKEN, "") ?: ""
    }

    fun getPersonId(): String {
        return prefs.getString(KEY_PERSON_ID, "") ?: ""
    }

    fun getPersonName(): String {
        return prefs.getString(PERSON_NAME, "") ?: ""
    }

    fun getPersonRole(): String {
        return prefs.getString(PERSON_ROLE, "") ?: ""
    }

    fun getLastUpdateTime(): Long {
        return prefs.getLong(LAST_UPDATE_TIME, 0L)
    }

    fun getClassNumber(): String {
        return prefs.getString(CLASS_NUMBER, "") ?: ""
    }

    fun saveClassNumber(classNumber: String) {
        prefs.edit().putString(CLASS_NUMBER, classNumber).apply()
    }

    fun savePersonId(studentId: String) {
        prefs.edit().putString(KEY_PERSON_ID, studentId).apply()
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun saveAuthTokenExpires(date: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN_EXPIRES, date).apply()
    }

    fun savePersonName(name: String) {
        prefs.edit().putString(PERSON_NAME, name).apply()
    }

    fun savePersonRole(name: String) {
        prefs.edit().putString(PERSON_ROLE, name).apply()
    }

    fun saveLastUpdateTime(time: Long) {
        prefs.edit().putLong(LAST_UPDATE_TIME, time).apply()
    }

    companion object {
        const val PREFS_NAME = "niceeljur"
        const val KEY_AUTH_TOKEN = "jwt_token"
        const val KEY_AUTH_TOKEN_EXPIRES = "jwt_token_expires"
        const val KEY_PERSON_ID = "person_id"
        const val CLASS_NUMBER = "class_number"
        const val PERSON_NAME = "person_name"
        const val PERSON_ROLE = "person_role"
        const val LAST_UPDATE_TIME = "last_person_info_update"
    }
} 