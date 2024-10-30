package com.example.evaluacionsegundoparcial

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class VideoActivity : AppCompatActivity() {
    private val REQUEST_VIDEO_CAPTURE = 1
    private val REQUEST_PERMISSIONS = 100
    private lateinit var btnPlayPauseVideo: Button
    private lateinit var videoView: VideoView
    private var isVideoPlaying = false
    private var currentVideoUri: Uri? = null // Para almacenar la URI del video actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        videoView = findViewById(R.id.videoView)
        btnPlayPauseVideo = findViewById(R.id.btn_play_pause_video)

        // Solicitar permisos
        requestCameraAndStoragePermissions()

        findViewById<Button>(R.id.btnGrabarVideo).setOnClickListener {
            // Solo se llama al método si los permisos ya están concedidos
            if (hasPermissions()) {
                startVideoCapture()
            } else {
                Toast.makeText(this, "Se requieren permisos para grabar video", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btnPlayPauseVideo.setOnClickListener {
            toggleVideoPlayback()
        }
    }

    private fun hasPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val recordAudioPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                recordAudioPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraAndStoragePermissions() {
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                REQUEST_PERMISSIONS
            )
        }
    }

    private fun startVideoCapture() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (takeVideoIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
        } else {
            Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Se requieren permisos para grabar video", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            val videoUri: Uri? = data?.data
            videoUri?.let {
                // Guardar el video en el almacenamiento interno
                val savedVideoUri = saveVideoToInternalStorage(it)
                if (savedVideoUri != null) {
                    currentVideoUri = savedVideoUri // Almacena la URI del video guardado
                    Log.d("VideoActivity", "Video guardado en: $savedVideoUri") // Log para verificar la URI

                    // Configura el MediaController
                    val mediaController = MediaController(this)
                    mediaController.setAnchorView(videoView)
                    videoView.setMediaController(mediaController)

                    // Cargar y reproducir un video desde raw
                    val rawVideoUri = Uri.parse("android.resource://${packageName}/raw/video_example")
                    videoView.setVideoURI(rawVideoUri)

                    videoView.requestFocus()

                    isVideoPlaying = false // Estado inicial es "no reproducido"
                    btnPlayPauseVideo.text = "Play Video" // Cambia el texto del botón
                } else {
                    Toast.makeText(this, "Error al guardar el video", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveVideoToInternalStorage(it: Uri): Uri {
        val videoFile = File(filesDir, "video_${System.currentTimeMillis()}.mp4")
        val inputStream: InputStream? = contentResolver.openInputStream(it)
        val outputStream = FileOutputStream(videoFile)

        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        return Uri.fromFile(videoFile)
    }

    private fun toggleVideoPlayback() {
        if (currentVideoUri != null) { // Asegúrate de que hay un video cargado
            if (isVideoPlaying) {
                videoView.pause()
                btnPlayPauseVideo.text = "Play Video"
            } else {
                videoView.requestFocus()
                videoView.start() // Comienza la reproducción
                btnPlayPauseVideo.text = "Pause Video"
            }
            isVideoPlaying = !isVideoPlaying // Alterna el estado de reproducción
        } else {
            Toast.makeText(this, "No hay video para reproducir", Toast.LENGTH_SHORT).show()
        }
    }
}
