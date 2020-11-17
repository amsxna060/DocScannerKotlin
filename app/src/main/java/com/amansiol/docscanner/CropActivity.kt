package com.amansiol.docscanner

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class CropActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var imageNumberTextView: TextView

    private lateinit var currentImageToCrop: ImageView

    companion object {
        var currentFile: File? = null
        var currentBitmap: Bitmap? = null
        var currentImageNumber: Int = 0
    }

    override fun onBackPressed() {
        val intent: Intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        imageNumberTextView = findViewById(R.id.image_number)
        currentImageToCrop = findViewById(R.id.current_image_crop)
        progressBar = findViewById(R.id.image_load_progressbar)

        currentImageNumber = intent.getIntExtra("modified", 0)
        setImageNumberText()
        resetCurrentFile()
        currentBitmap =
            BitmapFactory.decodeFile(currentFile?.absolutePath)
        currentImageToCrop.setImageBitmap(currentBitmap)
    }

    fun switchNext(view: View) {

        val task: SaveAndLoadOther = SaveAndLoadOther(1, progressBar)

        task.setBitmap(currentBitmap)
        task.setCurrentImageView(currentImageToCrop)
        task.setFile(currentFile)
        task.setImageTextView(imageNumberTextView)

        task.execute()
    }

    fun switchPrev(view: View) {
        val task: SaveAndLoadOther = SaveAndLoadOther(-1, progressBar)

        task.setBitmap(currentBitmap)
        task.setCurrentImageView(currentImageToCrop)
        task.setFile(currentFile)
        task.setImageTextView(imageNumberTextView)

        task.execute()
    }

    fun deleteImageFromArray(view: View) {
        CreatingPdf.bitmapFileArray.removeAt(currentImageNumber)

        if (CreatingPdf.bitmapFileArray.size == 0) {
            finish()
            return
        }

        BitmapFactory.decodeFile(currentFile?.absolutePath)
        currentImageToCrop.setImageBitmap(currentBitmap)

        setImageNumberText()
    }

    private fun setImageNumberText() {
        val imageNumberText = "${currentImageNumber + 1}/${CreatingPdf.bitmapFileArray.size}"
        imageNumberTextView.text = imageNumberText
    }

    fun rotateImageRight(view: View) {
        currentBitmap = rotateBitmap(currentBitmap!!, 90F)!!
        currentImageToCrop.setImageBitmap(currentBitmap)
    }

    fun rotateImageLeft(view: View) {
        currentBitmap = rotateBitmap(currentBitmap!!, -90F)!!
        currentImageToCrop.setImageBitmap(currentBitmap)
    }

    fun retakeImage(view: View) {
        val intent: Intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("position_in_array", currentImageNumber)
        startActivity(intent)
        finish()
    }

    fun cropImage(view: View) {
//        val task: SaveCurrentAndMoveToCropImage = SaveCurrentAndMoveToCropImage(this)
//        task.execute()
        val intent: Intent = Intent(this@CropActivity, CropImageActivity::class.java)
        intent.putExtra("position_in_array", currentImageNumber)
        startActivity(intent)
        finish()
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun resetCurrentFile() {
        currentFile = CreatingPdf.bitmapFileArray[currentImageNumber]
    }

    private fun saveCurrentBitmapToFile() {
        val task: SaveBitmapAsync = SaveBitmapAsync(currentBitmap, currentFile)
        task.execute()
    }

    fun startFilterActivity(view: View) {
        saveCurrentBitmapToFile()
        val intent: Intent = Intent(this, FilterActivity::class.java)
        startActivity(intent)
        finish()
    }
}