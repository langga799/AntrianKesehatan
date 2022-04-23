package com.example.antriankesehatan.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.antriankesehatan.MainActivity
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.ActivityLoginBinding
import com.example.antriankesehatan.ui.auth.register.RegisterActivity
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

@Suppress("CheckResult")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.btnLogin.isEnabled = false

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
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                    Toast.makeText(this, "send requst login", Toast.LENGTH_SHORT).show()
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
}