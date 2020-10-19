package com.amansiol.docscanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_creating_pdf.*

class CreatingPdf : AppCompatActivity() {

    companion object {
        val bitmapArray: ArrayList<Bitmap> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creating_pdf)

        captured_images_recyclerview.layoutManager = GridLayoutManager(this,2)
        captured_images_recyclerview.adapter = BitmapAdapter(this)

        startCapture.setOnClickListener(View.OnClickListener {
            val intent: Intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        })
    }

    fun savePdf(view: View) {

    }
}