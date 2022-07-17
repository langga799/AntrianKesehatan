package com.example.antriankesehatan.ui.auth.login

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.antriankesehatan.MainActivity
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.ActivityLoginBinding
import com.example.antriankesehatan.model.LoginResponse
import com.example.antriankesehatan.network.NetworkConfig
import com.example.antriankesehatan.ui.auth.register.RegisterActivity
import com.example.antriankesehatan.utils.SharedPreference
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("CheckResult")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var preference: SharedPreference
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.btnLogin.isEnabled = false

        preference = SharedPreference(this)

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(baseContext, RegisterActivity::class.java))
        }

        fieldStream()
    }

    private fun fieldStream() {
        val emailPatternStream = RxTextView.textChanges(binding.edtEmail)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailPatternStream.subscribe {
            emailValidation(it)
        }

        val passwordStream = RxTextView.textChanges(binding.edtPassword)
            .skipInitialValue()
            .map { password ->
                password.length < 6
            }
        passwordStream.subscribe {
            passwordValidation(it)
        }

        val invalidField = Observable.combineLatest(
            emailPatternStream,
            passwordStream
        ) { textEmail, textPassword ->
            !textEmail && !textPassword
        }
        invalidField.subscribe { isValidField ->
            if (isValidField) {
                binding.btnLogin.isEnabled = true
                binding.btnLogin.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.cyan_500))

                binding.btnLogin.setOnClickListener {
                    Toast.makeText(this, "send requst login", Toast.LENGTH_SHORT).show()
                    login()
                }

            } else {
                binding.btnLogin.isEnabled = false
                binding.btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            }
        }
    }


    private fun emailValidation(isValid: Boolean) {
        binding.edtLayoutEmail.error =
            if (isValid) getString(R.string.edt_error_email_valid) else null
    }

    private fun passwordValidation(isValid: Boolean) {
        binding.edtLayoutPassword.error =
            if (isValid) getString(R.string.password_text_length) else null
    }

    private fun login() {
        val token = preference.getToken()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.progress)
        val dialog: AlertDialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        NetworkConfig().getApiService().requestLogin("bearer $token", email, password)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>,
                ) {

                    when (response.code()) {
                        200 -> {
                            Toast.makeText(this@LoginActivity,
                                response.body()?.meta?.message,
                                Toast.LENGTH_SHORT).show()
                            dialog.dismiss()

                            preference.saveLogin(true)
                            preference.saveToken(response.body()?.data?.accessToken!!)
                            activityScope.launch {
                                delay(1000L)
                                startActivity(Intent(baseContext, MainActivity::class.java))
                                finishAffinity()
                            }
                        }
                        401 -> {
                            Toast.makeText(this@LoginActivity,
                                response.body()?.meta?.message,
                                Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        500 -> {
                            Toast.makeText(this@LoginActivity,
                                response.body()?.meta?.message,
                                Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }


                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }

            })
    }
}