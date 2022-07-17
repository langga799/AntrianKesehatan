package com.example.antriankesehatan.utilsimport android.content.Contextclass SharedPreference(context: Context) {    companion object {        private const val PREF_NAME = "com.example.antriankesehatan.shared.preference"        private const val BEARER_TOKEN = "bearer_token"        private const val IS_LOGIN = "is_login"        private const val STATE_WORK_ALARM = "state_work_alarm"    }    private val preference = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)    private val editor = preference.edit()    fun saveToken(token: String) {        editor.apply {            putString(BEARER_TOKEN, token)            apply()        }    }    fun saveLogin(isLogin: Boolean) {        editor.apply {            putBoolean(IS_LOGIN, isLogin)            apply()        }    }    fun saveWorkAlarm(state: Boolean) {        editor.apply {            putBoolean(STATE_WORK_ALARM, state)            apply()        }    }    /** ====================================     *  GET VALUE FROM SHARED PREFERENCE     *  ====================================     */    fun getToken(): String {        return preference.getString(BEARER_TOKEN, "").toString()    }    fun getLogin(): Boolean {        return preference.getBoolean(IS_LOGIN, false)    }    fun getStateWorkAlarm(): Boolean {        return preference.getBoolean(STATE_WORK_ALARM, false)    }    /** ====================================     *  REMOVE LOGGING FROM SHARED PREFERENCE     *  ====================================     */    fun clear() {        editor.apply {            remove(BEARER_TOKEN)            remove(IS_LOGIN)            remove(STATE_WORK_ALARM)            apply()        }    }}