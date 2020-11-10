package com.amansiol.docscanner

import android.graphics.Bitmap
import android.os.AsyncTask
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SaveBitmapAsync(var bitmap: Bitmap, var file: File) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}