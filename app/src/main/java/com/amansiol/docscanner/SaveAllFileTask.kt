package com.amansiol.docscanner

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import net.alhazmy13.imagefilter.ImageFilter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SaveAllFileTask(var context: Context, var filterArrayList: ArrayList<Int>) : AsyncTask<Void, Void, Void>() {
    private lateinit var progressDialog: ProgressDialog

    override fun onPreExecute() {
        super.onPreExecute()
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Saving, Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }

    override fun doInBackground(vararg params: Void?): Void? {
        for (i in 0 until CreatingPdf.bitmapFileArray.size) {
            var bitmap: Bitmap = BitmapFactory.decodeFile(CreatingPdf.bitmapFileArray[i].absolutePath)
            bitmap = if (filterArrayList[i] == 1) {
                ImageFilter.applyFilter(bitmap, ImageFilter.Filter.BLOCK)
            } else if(filterArrayList[i] == 2) {
                ImageFilter.applyFilter(bitmap, ImageFilter.Filter.GRAY)
            } else if (filterArrayList[i] == 3) {
                ImageFilter.applyFilter(bitmap, ImageFilter.Filter.HDR)
            } else {
                ImageFilter.applyFilter(bitmap, ImageFilter.Filter.SOFT_GLOW)
            }

            var smallBitmap: Bitmap = decodeSampledBitmapFromFile(CreatingPdf.bitmapFileArray[i],40,40)
            smallBitmap = if (filterArrayList[i] == 1) {
                ImageFilter.applyFilter(smallBitmap, ImageFilter.Filter.BLOCK)
            } else if(filterArrayList[i] == 2) {
                ImageFilter.applyFilter(smallBitmap, ImageFilter.Filter.GRAY)
            } else if (filterArrayList[i] == 3) {
                ImageFilter.applyFilter(smallBitmap, ImageFilter.Filter.HDR)
            } else {
                ImageFilter.applyFilter(smallBitmap, ImageFilter.Filter.SOFT_GLOW)
            }

            CreatingPdf.bitmapArray[i] = smallBitmap
            try {
                FileOutputStream(CreatingPdf.bitmapFileArray[i]).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        progressDialog.dismiss()
        val intent: Intent = Intent(context, CreatingPdf::class.java)
        context.startActivity(intent)
        val currentActivity = context as Activity
        currentActivity.finish()
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun decodeSampledBitmapFromFile(
        file: File,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.absolutePath,this);

            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false

            BitmapFactory.decodeFile(file.absolutePath,this)
        }
    }
}