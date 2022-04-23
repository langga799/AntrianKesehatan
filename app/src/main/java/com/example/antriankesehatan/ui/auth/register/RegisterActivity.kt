package com.example.antriankesehatan.ui.auth.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.ActivityRegisterBinding
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
@SuppressLint("CheckResult")
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.btnRegister.isEnabled = false
        fieldStream()


    }


    private fun fieldStream(){
        val nameStream = RxTextView.textChanges(binding.edtName)
            .skipInitialValue()
            .map { name ->
                name.isEmpty()
            }
        nameStream.subscribe {
            nameValidation(it)
        }

        val phoneStream = RxTextView.textChanges(binding.edtTelp)
            .skipInitialValue()
            .map { phone ->
                phone.isEmpty()
            }
        phoneStream.subscribe {
            phoneValidation(it)
        }

        chooseGender()

        val addressStream = RxTextView.textChanges(binding.edtAddress)
            .skipInitialValue()
            .map { address ->
                address.isEmpty()
            }
        addressStream.subscribe {
            addressValidation(it)
        }

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


        val invalidFieldStream = Observable.combineLatest(
            nameStream,
            phoneStream,
            addressStream,
            emailPatternStream,
            passwordStream
        ) { textName, textPhone, textAddress, textEmail, textPassword ->
            !textName && !textPhone && !textAddress && !textEmail && !textPassword
        }

        invalidFieldStream.subscribe { isValid: Boolean ->
            if (isValid) {
                binding.btnRegister.isEnabled = true
                binding.btnRegister.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.cyan_500))
                binding.btnRegister.setOnClickListener {
                    Toast.makeText(this, "send requst", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.btnRegister.isEnabled = false
                binding.btnRegister.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            }
        }
    }


    private fun nameValidation(isValid: Boolean) {
        binding.edtLayoutName.error = if (isValid) getString(R.string.edt_error_name) else null
    }

    private fun phoneValidation(isValid: Boolean) {
        binding.edtLayoutTelp.error = if (isValid) getString(R.string.edt_error_phone) else null
    }

    private fun addressValidation(isValid: Boolean) {
        binding.edtLayoutAddress.error =
            if (isValid) getString(R.string.edt_error_address) else null
    }

    private fun emailValidation(isValid: Boolean) {
        binding.edtLayoutEmail.error =
            if (isValid) getString(R.string.edt_error_email_valid) else null
    }

    private fun passwordValidation(isValid: Boolean) {
        binding.edtLayoutPassword.error =
            if (isValid) getString(R.string.password_text_length) else null
    }


    private fun chooseGender() {
        val itemGender = listOf(
            getString(R.string.text_male),
            getString(R.string.text_female),
        )
        val adapter = ArrayAdapter(this, R.layout.list_item_gender, itemGender)
        (binding.edtLayoutGender.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }
}