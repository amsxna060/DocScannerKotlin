package com.amansiol.docscanner

import android.graphics.Bitmap
import android.os.AsyncTask
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SaveBitmapAsync(var bitmap: Bitmap?, var file: File?) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
        try {
            FileOutputStream(CropActivity.currentFile).use { out ->
                CropActivity.currentBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
                CropActivity.currentBitmap = null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}