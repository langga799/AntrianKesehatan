package com.example.antriankesehatan.ui.auth.register

import android.annotation.SuppressLint
import android.content.Intent
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
import com.example.antriankesehatan.model.RegisterResponse
import com.example.antriankesehatan.model.User
import com.example.antriankesehatan.network.NetworkConfig
import com.example.antriankesehatan.ui.auth.login.LoginActivity
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

@SuppressLint("CheckResult")
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var preference: SharedPreference
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        preference = SharedPreference(this)
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
                    Toast.makeText(this, "Send request", Toast.LENGTH_SHORT).show()

                    register()
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

    private fun register() {
        val name = binding.edtName.text.toString()
        val telp = binding.edtTelp.text.toString()
        val gender = binding.edtGender.text.toString()
        val address = binding.edtAddress.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val passwordConfirmation = binding.edtPassword.text.toString()

        Log.d("dataaaaa", telp)

        NetworkConfig().getApiService().requestRegister(
            name,
            email,
            password,
            passwordConfirmation,
            telp,
            gender,
            address
        ).enqueue(object : Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>,
            ) {
                if (response.isSuccessful){
                    Toast.makeText(this@RegisterActivity, "Register berhasil", Toast.LENGTH_SHORT).show()

                    preference.saveToken(response.body()?.data?.accessToken!!)

                    activityScope.launch {
                        delay(1000L)
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Register gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })

    }
}