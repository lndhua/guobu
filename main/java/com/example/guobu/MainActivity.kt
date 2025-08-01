package com.example.photomanager

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.guobu.R
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PERMISSIONS = 2
    private lateinit var imageView: ImageView
    private lateinit var editText: EditText
    private lateinit var dbHandler: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        editText = findViewById(R.id.editText)
        dbHandler = DatabaseHandler(this)

        val captureButton: Button = findViewById(R.id.captureButton)
        captureButton.setOnClickListener { dispatchTakePictureIntent() }

        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.setOnClickListener { saveInfoToDatabase() }

        val viewButton: Button = findViewById(R.id.viewButton)
        viewButton.setOnClickListener { viewSavedInfos() }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSIONS)
        }
    }

    private fun dispatchTakePictureIntent() {
        checkPermissions()
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            saveImageToExternalStorage(imageBitmap)
        }
    }

    private fun saveImageToExternalStorage(bitmap: Bitmap): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "photo.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PhotoManager")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.also { uri ->
            try {
                contentResolver.openOutputStream(uri)?.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
                }
            } catch (e: IOException) {
                Toast.makeText(this, "Failed to save photo", Toast.LENGTH_SHORT).show()
                contentResolver.delete(uri, null, null)
            }
        }
    }

    private fun saveInfoToDatabase() {
        val info = editText.text.toString()
        if (info.isNotEmpty()) {
            dbHandler.addInfo(info)
            Toast.makeText(this, "Info saved", Toast.LENGTH_SHORT).show()
            editText.setText("")
        } else {
            Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show()
        }
    }

    private fun viewSavedInfos() {
        val allInfos = dbHandler.getAllInfos()
        val message = StringBuilder()
        for (info in allInfos) {
            message.append("$info\n")
        }
        Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show()
    }
}