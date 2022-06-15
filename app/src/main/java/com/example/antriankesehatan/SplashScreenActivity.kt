package com.example.antriankesehatan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.antriankesehatan.ui.auth.login.LoginActivity
import com.example.antriankesehatan.ui.auth.register.RegisterActivity
import com.example.antriankesehatan.utils.SharedPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val activityScope = CoroutineScope(Dispatchers.Main)
    private lateinit var preference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        preference = SharedPreference(this)

        Log.d("login-info", preference.getLogin().toString())
        when(preference.getLogin()){
            true -> {
                activityScope.launch {
                    delay(1000L)
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                    finishAffinity()
                }
            }
            false -> {
                activityScope.launch {
                    delay(1000L)
                    startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                    finishAffinity()
                }
            }
        }
    }

}