package com.amansiol.docscanner

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CropImageActivity : AppCompatActivity() {

    lateinit var cropImageView: CropImageView
    lateinit var currentFile: File
    var pos: Int = 0

    override fun onBackPressed() {
        val intent: Intent = Intent(this, CropActivity::class.java)
        intent.putExtra("modified", pos)
        startActivity(intent)
        finish()
    }

    fun getCroppedImage(view: View) {
        CropActivity.currentBitmap = cropImageView.croppedImage
        saveCroppedToFile()

        val intent: Intent = Intent(this, CropActivity::class.java)
        intent.putExtra("modified", pos)
        startActivity(intent)
        finish()
    }

    private fun saveCroppedToFile() {
        try {
            FileOutputStream(currentFile).use { out ->
                CropActivity.currentBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_image)

        cropImageView = findViewById(R.id.cropImageView)
        cropImageView.setImageBitmap(CropActivity.currentBitmap)

        pos = intent.getIntExtra("position_in_array",0)
        currentFile = CreatingPdf.bitmapFileArray[pos]
    }
}