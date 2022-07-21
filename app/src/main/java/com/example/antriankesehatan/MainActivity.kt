package com.example.antriankesehatan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.WorkManager
import com.example.antriankesehatan.databinding.ActivityMainBinding
import com.example.antriankesehatan.pusher.NotificationPusher
import com.example.antriankesehatan.pusher.ServiceNotification
import com.example.antriankesehatan.ui.pesan.MessageActivity
import com.example.antriankesehatan.utils.gone
import com.example.antriankesehatan.utils.visible
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0
    private var backToast: Toast? = null


    private val notificationPusher = NotificationPusher

    private lateinit var workManager: WorkManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.elevation = 0F

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.doctorFragment,
                R.id.antrianFragment,
                R.id.riwayatFragment,
                R.id.profileFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    navView.visible()
                }
                R.id.doctorFragment -> {
                    navView.visible()
                }
                R.id.antrianFragment -> {
                    navView.visible()
                }
                R.id.riwayatFragment -> {
                    navView.visible()
                }
                R.id.profileFragment -> {
                    navView.visible()
                }
                R.id.changeProfileFragment -> {
                    navView.gone()
                }
                R.id.jadwalFragment -> {
                    navView.gone()
                }
                R.id.changePhotoFragment -> {
                    navView.gone()
                }
            }
        }



        CoroutineScope(Dispatchers.Default).launch {
            val intent = Intent(this@MainActivity, ServiceNotification::class.java)
            startService(intent)
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_message, menu)
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.message_menu -> {
                startActivity(Intent(this, MessageActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast?.cancel()
            super.onBackPressed()
            return
        } else {
            backToast = Toast.makeText(baseContext, "Tekan lagi untuk keluar", Toast.LENGTH_SHORT)
            backToast?.show()
        }

        backPressedTime = System.currentTimeMillis()

    }


    override fun onDestroy() {
        CoroutineScope(Dispatchers.Default).launch {
            val intent = Intent(this@MainActivity, ServiceNotification::class.java)
            startService(intent)
        }
        super.onDestroy()

    }





}