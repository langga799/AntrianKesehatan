package com.example.antriankesehatan.ui.pesan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.antriankesehatan.MainActivity
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.ActivityMessageBinding

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Pesan Masuk"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
        return super.onOptionsItemSelected(item)
    }


}