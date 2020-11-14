package com.amansiol.docscanner

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import net.alhazmy13.imagefilter.ImageFilter
import java.io.File

class ReturnBitmapArray(var filterArrayList: ArrayList<Int>) : AsyncTask<Void, Void, ArrayList<Bitmap>>() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: Void?): ArrayList<Bitmap> {

        var bitmapArray: ArrayList<Bitmap> = ArrayList()
        for (i in 0 until CreatingPdf.bitmapFileArray.size) {
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
        }

        return bitmapArray
    }

    override fun onPostExecute(result: ArrayList<Bitmap>?) {
        super.onPostExecute(result)
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
            BitmapFactory.decodeFile(file.absolutePath, this)

            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false

            BitmapFactory.decodeFile(file.absolutePath, this)
        }
    }
}