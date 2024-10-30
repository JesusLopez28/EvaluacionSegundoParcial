package com.example.evaluacionsegundoparcial

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.widget.ImageView
import android.widget.TextView
import android.util.Log

class SensorActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var tiltSensor: Sensor? = null

    // Referencias de UI
    private lateinit var tiltTextView: TextView
    private lateinit var imgChange: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        // Comprobar permisos de notificación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        // Inicializar SensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        tiltSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)

        // Referencias de UI
        tiltTextView = findViewById(R.id.tvSensorStatus)
        imgChange = findViewById(R.id.imageViewChange)

        // Registrar el sensor de inclinación
        tiltSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        createNotificationChannel() // Crear canal de notificación
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_GAME_ROTATION_VECTOR) {
                val tilt = it.values[0] // Valor de inclinación en el eje X
                handleTiltSensor(tilt)
            }
        }
    }

    private fun handleTiltSensor(tilt: Float) {
        // Actualizar el TextView con el valor de inclinación
        tiltTextView.text = "Inclinación: $tilt"

        // Cambiar la imagen según la inclinación
        if (tilt > 0.5) {
            imgChange.setImageResource(R.drawable.image_one)
        } else {
            sendNotification("Inclinación Baja", "Inclinación detectada: $tilt") // Enviar notificación
            imgChange.setImageResource(R.drawable.image_two)
        }

        Log.d("TiltSensor", "Inclinación detectada, valor: $tilt")
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val channelId = "tilt_sensor_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_sensor) // Cambia esto por tu icono
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "tilt_sensor_channel"
            val channelName = "Tilt Sensor Notifications"
            val channelDescription = "Notifications for tilt sensor events"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onPause() {
        super.onPause()
        // Desregistrar el sensor al pausar la actividad
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        // Volver a registrar el sensor activo al reanudar la actividad
        tiltSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
}
