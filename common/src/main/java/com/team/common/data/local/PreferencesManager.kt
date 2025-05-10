package com.team.common.data.local

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

    fun getStudentId(): String {
        return prefs.getString(KEY_STUDENT_ID, "") ?: ""
    }

    fun getStudentName(): String {
        return prefs.getString(STUDENT_NAME, "") ?: ""
    }

    fun getLastUpdateTime(): Long {
        return prefs.getLong(LAST_UPDATE_TIME, 0L)
    }

    fun saveStudentId(studentId: String) {
        prefs.edit().putString(KEY_STUDENT_ID, studentId).apply()
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun saveStudentName(name: String) {
        prefs.edit().putString(STUDENT_NAME, name).apply()
    }

    fun saveLastUpdateTime(time: Long) {
        prefs.edit().putLong(LAST_UPDATE_TIME, time).apply()
    }

    companion object {
        const val PREFS_NAME = "niceeljur"
        const val KEY_AUTH_TOKEN = "jwt_token"
        const val KEY_STUDENT_ID = "student_id"
        const val STUDENT_NAME = "student_name"
        const val LAST_UPDATE_TIME = "last_student_info_update"
    }
} 