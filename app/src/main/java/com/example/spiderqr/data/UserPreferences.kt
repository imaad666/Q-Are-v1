package com.example.spiderqr.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("qare_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ROLL_NUMBER = "roll_number"
        private const val KEY_STUDENT_NAME = "student_name"
        private const val KEY_IS_SETUP_COMPLETE = "is_setup_complete"
        private const val KEY_QR_COLOR = "qr_color"
        private const val KEY_QR_CORNER_RADIUS = "qr_corner_radius"
        private const val KEY_USER_INITIALS = "user_initials"
    }

    fun saveUserData(rollNumber: String, studentName: String) {
        prefs.edit()
            .putString(KEY_ROLL_NUMBER, rollNumber)
            .putString(KEY_STUDENT_NAME, studentName)
            .putBoolean(KEY_IS_SETUP_COMPLETE, true)
            .apply()
    }

    fun getRollNumber(): String? = prefs.getString(KEY_ROLL_NUMBER, null)
    fun getStudentName(): String? = prefs.getString(KEY_STUDENT_NAME, null)
    fun isSetupComplete(): Boolean = prefs.getBoolean(KEY_IS_SETUP_COMPLETE, false)

    fun saveQRColor(color: Int) {
        prefs.edit().putInt(KEY_QR_COLOR, color).apply()
    }

    fun getQRColor(): Int = prefs.getInt(KEY_QR_COLOR, android.graphics.Color.BLACK)

    fun saveCornerRadius(radius: Float) {
        prefs.edit().putFloat(KEY_QR_CORNER_RADIUS, radius).apply()
    }

    fun getCornerRadius(): Float = prefs.getFloat(KEY_QR_CORNER_RADIUS, 0f)

    fun saveUserInitials(initials: String) {
        prefs.edit().putString(KEY_USER_INITIALS, initials).apply()
    }

    fun getUserInitials(): String? = prefs.getString(KEY_USER_INITIALS, null)

    fun resetUserData() {
        prefs.edit().clear().apply()
    }
}
