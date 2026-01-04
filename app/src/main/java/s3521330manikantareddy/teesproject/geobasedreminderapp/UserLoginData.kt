package s3521330manikantareddy.teesproject.geobasedreminderapp

import android.content.Context


object UserLoginData {

    private const val PREFS_NAME = "POST_PREFS"
    private const val KEY_IS_USER_LOGGED_IN = "KEY_IS_USER_LOGGED_IN"
    private const val KEY_NAME = "KEY_NAME"
    private const val KEY_EMAIL = "KEY_EMAIL"
    private const val KEY_PLACE = "KEY_PLACE"
    private const val KEY_DOB = "KEY_DOB"

    fun markLoginStatus(context: Context, isLoggedIn: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_USER_LOGGED_IN, isLoggedIn).apply()
    }

    fun checkLoginStatus(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_USER_LOGGED_IN, false)
    }

    fun saveName(context: Context, name: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NAME, name).apply()
    }

    fun getName(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_NAME, "") ?: ""
    }

    fun saveEmail(context: Context, email: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_EMAIL, email).apply()
    }

    fun getEmail(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_EMAIL, "") ?: ""
    }

    fun savePlace(context: Context, place: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PLACE, place).apply()
    }

    fun getPlace(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PLACE, "") ?: ""
    }

    fun saveDOB(context: Context, dob: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_DOB, dob).apply()
    }

    fun getDOB(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_DOB, "") ?: ""
    }
}