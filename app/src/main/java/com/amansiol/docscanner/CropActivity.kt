package com.amansiol.docscanner

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class CropActivity : AppCompatActivity() {

    var currentImageNumber: Int = 0
    lateinit var currentImageToCrop: ImageView
    lateinit var currentFile: File
    private lateinit var imageNumberTextView: TextView

    companion object{
        lateinit var currentBitmap: Bitmap
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

        currentImageNumber = intent.getIntExtra("modified", 0)

        setImageNumberText()

        resetCurrentFile()

        currentBitmap =
            BitmapFactory.decodeFile(currentFile.absolutePath)
        currentImageToCrop.setImageBitmap(currentBitmap)
    }

    fun switchNext(view: View) {
        saveCurrentBitmapToFile()

        if (currentImageNumber < CreatingPdf.bitmapArray.size - 1) {
            currentImageNumber++

            resetCurrentFile()
            currentBitmap =
                BitmapFactory.decodeFile(currentFile.absolutePath)
            currentImageToCrop.setImageBitmap(currentBitmap)

            setImageNumberText()
        }
    }

    fun switchPrev(view: View) {

        saveCurrentBitmapToFile()

        if (currentImageNumber > 0) {
            currentImageNumber--

            resetCurrentFile()
            currentBitmap =
                BitmapFactory.decodeFile(currentFile.absolutePath)
            currentImageToCrop.setImageBitmap(currentBitmap)

            setImageNumberText()
        }
    }

    fun deleteImageFromArray(view: View) {
        CreatingPdf.bitmapArray.removeAt(currentImageNumber)
        CreatingPdf.bitmapFileArray.removeAt(currentImageNumber)

        if (CreatingPdf.bitmapArray.size == 0) {
            finish()
            return
        }

        BitmapFactory.decodeFile(currentFile.absolutePath)
        currentImageToCrop.setImageBitmap(currentBitmap)

        setImageNumberText()
    }

    private fun setImageNumberText() {
        val imageNumberText = "${currentImageNumber + 1}/${CreatingPdf.bitmapFileArray.size}"
        imageNumberTextView.text = imageNumberText
    }

    fun rotateImageRight(view: View) {
        currentBitmap = rotateBitmap(currentBitmap,90F)!!
        currentImageToCrop.setImageBitmap(currentBitmap)
    }

    fun rotateImageLeft(view: View) {
        currentBitmap = rotateBitmap(currentBitmap,-90F)!!
        currentImageToCrop.setImageBitmap(currentBitmap)
    }

    fun retakeImage(view: View) {
        val intent: Intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("position_in_array",currentImageNumber)
        startActivity(intent)
        finish()
    }

    fun cropImage(view: View) {
        saveCurrentBitmapToFile()
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
        val intent: Intent = Intent(this,FilterActivity::class.java)
        startActivity(intent)
        finish()
    }
}