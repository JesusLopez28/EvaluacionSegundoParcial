package com.example.evaluacionsegundoparcial

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // MainActivity.kt (onCreate)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("sensor_channel", "Sensor Notifications", NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_catalogo -> {
                startActivity(Intent(this, CatalogActivity::class.java))
                true
            }

            R.id.action_video -> {
                startActivity(Intent(this, VideoActivity::class.java))
                true
            }

            R.id.action_sensor -> {
                startActivity(Intent(this, SensorActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

