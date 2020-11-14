package com.amansiol.docscanner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import java.io.FileOutputStream
import java.io.IOException

class SaveCurrentAndMoveToCropImage(var context: Context) : AsyncTask<Void,Void, Void?>() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            FileOutputStream(CropActivity.currentFile).use { out ->
                CropActivity.currentBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)

        val intent = Intent(context, CropImageActivity::class.java)
        intent.putExtra("position_in_array", CropActivity.currentImageNumber)
        context.startActivity(intent)
        (context as Activity).finish()
    }
}