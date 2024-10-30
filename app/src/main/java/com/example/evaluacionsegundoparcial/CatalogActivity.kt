package com.example.evaluacionsegundoparcial

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap

// CatalogActivity.kt
class CatalogActivity : AppCompatActivity() {
    private val bicicletas = mutableListOf<Bicicleta>()
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val marca = findViewById<EditText>(R.id.etMarca).text.toString()
            val modelo = findViewById<EditText>(R.id.etModelo).text.toString()
            val año = findViewById<EditText>(R.id.etAño).text.toString().toInt()
            val color = findViewById<EditText>(R.id.etColor).text.toString()

            // Guardar imagen
            val imagen = findViewById<ImageView>(R.id.ivFotoBicicleta).drawable.toBitmap()

            val bicicleta = Bicicleta(marca, modelo, año, color, imagen)
            bicicletas.add(bicicleta)
            Toast.makeText(this, "Bicicleta guardada", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnLimpiar).setOnClickListener {
            findViewById<EditText>(R.id.etMarca).setText("")
            findViewById<EditText>(R.id.etModelo).setText("")
            findViewById<EditText>(R.id.etAño).setText("")
            findViewById<EditText>(R.id.etColor).setText("")
            findViewById<ImageView>(R.id.ivFotoBicicleta).setImageResource(android.R.color.transparent)
        }

        findViewById<Button>(R.id.btnBuscar).setOnClickListener {
            val modeloBuscar = findViewById<EditText>(R.id.etModeloBuscar).text.toString()
            val bicicleta = bicicletas.find { it.modelo == modeloBuscar }
            bicicleta?.let {
                // Mostrar información de la bicicleta encontrada
                findViewById<EditText>(R.id.etMarca).setText(it.marca)
                findViewById<EditText>(R.id.etModelo).setText(it.modelo)
                findViewById<EditText>(R.id.etAño).setText(it.año.toString())
                findViewById<EditText>(R.id.etColor).setText(it.color)
                findViewById<ImageView>(R.id.ivFotoBicicleta).setImageBitmap(it.imagen)
            } ?: Toast.makeText(this, "Bicicleta no encontrada", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnCapturarImagen).setOnClickListener {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            findViewById<ImageView>(R.id.ivFotoBicicleta).setImageBitmap(imageBitmap)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
