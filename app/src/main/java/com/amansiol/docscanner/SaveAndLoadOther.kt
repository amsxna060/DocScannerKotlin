package com.amansiol.docscanner

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SaveAndLoadOther(
    var action: Int,
    var progressBar: ProgressBar
) : AsyncTask<Void, Void, Void>() {

    private var currentBitmap: Bitmap? = null
    private var currentImageView: ImageView? = null
    private var currentFile: File? = null
    private var imageNumberTextView: TextView? = null
    fun setBitmap(bitmap: Bitmap?) {
        currentBitmap = bitmap
    }

    fun setCurrentImageView(currentImageView: ImageView) {
        this.currentImageView = currentImageView
    }

    fun setFile(currentFile: File?) {
        this.currentFile = currentFile
    }

    fun setImageTextView(textView: TextView) {
        imageNumberTextView = textView
    }

    override fun onPreExecute() {
        super.onPreExecute()
        progressBar.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            FileOutputStream(CropActivity.currentFile!!).use { out ->
                CropActivity.currentBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }

            if (action == -1) {
                if (CropActivity.currentImageNumber > 0) {
                    CropActivity.currentBitmap = null
                    CropActivity.currentImageNumber--
                    CropActivity.currentFile = CreatingPdf.bitmapFileArray[CropActivity.currentImageNumber]
                    CropActivity.currentBitmap = BitmapFactory.decodeFile(CropActivity.currentFile?.absolutePath)
                }

            } else {
                if (CropActivity.currentImageNumber < CreatingPdf.bitmapFileArray.size - 1) {
                    CropActivity.currentBitmap = null
                    CropActivity.currentImageNumber++
                    CropActivity.currentFile = CreatingPdf.bitmapFileArray[CropActivity.currentImageNumber]
                    CropActivity.currentBitmap = BitmapFactory.decodeFile(CropActivity.currentFile?.absolutePath)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
       return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        progressBar.visibility = View.INVISIBLE

        currentImageView?.setImageBitmap(CropActivity.currentBitmap)

        setImageNumberText()
    }

    private fun setImageNumberText() {
        val imageNumberText = "${CropActivity.currentImageNumber + 1}/${CreatingPdf.bitmapFileArray.size}"
        imageNumberTextView?.text = imageNumberText
    }
}