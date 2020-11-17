package com.amansiol.docscanner

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import net.alhazmy13.imagefilter.ImageFilter
import java.io.File

class FilterLoadOther(
    var currentFile: File,
    var currentImageNumber: Int,
    var progressBar: ProgressBar,
    var filterArrayList: ArrayList<Int>
) : AsyncTask<Void, Void, Void>() {

    private var noneBitmap: Bitmap? = null
    private var glowBitmap: Bitmap? = null
    private var blackBitmap: Bitmap? = null
    private var hdrBitmap: Bitmap? = null
    private var grayBitmap: Bitmap? = null

    private var currentBitmap: Bitmap? = null

    private var blackImageView: ImageView? = null
    private var hdrImageView: ImageView? = null
    private var glowImageView: ImageView? = null
    private var noneImageView: ImageView? = null
    private var grayImageView: ImageView? = null

    private var currentImageView: ImageView? = null

    override fun onPreExecute() {
        super.onPreExecute()
        progressBar.visibility = View.VISIBLE
    }

    fun setSmallBitmaps(
        noneBitmap: Bitmap?,
        blackBitmap: Bitmap?,
        grayBitmap: Bitmap?,
        hdrBitmap: Bitmap?,
        glowBitmap: Bitmap?
    ) {
        this.noneBitmap = noneBitmap
        this.grayBitmap = grayBitmap
        this.blackBitmap = blackBitmap
        this.hdrBitmap = hdrBitmap
        this.glowBitmap = glowBitmap
    }

    fun setSmallImageViews(
        noneImageView: ImageView,
        blackImageView: ImageView,
        grayImageView: ImageView,
        hdrImageView: ImageView,
        glowImageView: ImageView
    ) {
        this.noneImageView = noneImageView
        this.glowImageView = glowImageView
        this.hdrImageView = hdrImageView
        this.blackImageView = blackImageView
        this.grayImageView = grayImageView
    }

    fun setCurrentBitmap(currentBitmap: Bitmap?) {
        this.currentBitmap = currentBitmap
    }

    fun setCurrentImageView(currentImageView: ImageView?) {
        this.currentImageView = currentImageView
    }

    override fun doInBackground(vararg params: Void?): Void? {
        currentBitmap = null
        currentBitmap = BitmapFactory.decodeFile(currentFile.absolutePath)
        setAppropriateFilter()
        noneBitmap = null
        glowBitmap = null
        blackBitmap = null
        glowBitmap = null
        hdrBitmap = null

        noneBitmap = decodeSampledBitmapFromFile(currentFile, 40, 40)
        getAllPreviews()
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        progressBar.visibility = View.INVISIBLE

        currentImageView?.setImageBitmap(currentBitmap)

        blackImageView?.setImageBitmap(blackBitmap)
        grayImageView?.setImageBitmap(grayBitmap)
        glowImageView?.setImageBitmap(glowBitmap)
        noneImageView?.setImageBitmap(noneBitmap)
        hdrImageView?.setImageBitmap(hdrBitmap)
    }

    private fun getAllPreviews() {
        getBlackPreview()
        getGlowPreview()
        getGrayPreview()
        getNonePreview()
        getHDRPreview()
    }

    private fun getNonePreview() {
//        val file: File = File(Environment.getExternalStorageDirectory().absolutePath + "/${Constants.APP_FOLDER_NAME}/temp_$currentImageNumber.png")
//        noneBitmap = decodeSampledBitmapFromFile(file,40,40)
    }

    private fun getGlowPreview() {
        glowBitmap = ImageFilter.applyFilter(noneBitmap, ImageFilter.Filter.SOFT_GLOW)
    }

    private fun getHDRPreview() {
        hdrBitmap = ImageFilter.applyFilter(noneBitmap, ImageFilter.Filter.HDR)
    }

    private fun getGrayPreview() {
        grayBitmap = ImageFilter.applyFilter(noneBitmap, ImageFilter.Filter.GRAY)
    }

    private fun getBlackPreview() {
        blackBitmap = ImageFilter.applyFilter(noneBitmap, ImageFilter.Filter.BLOCK)
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
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

    private fun setAppropriateFilter() {
        var prevBitmap: Bitmap? = currentBitmap
        currentBitmap = if (filterArrayList[currentImageNumber] == 1) {
            ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.BLOCK)
        } else if (filterArrayList[currentImageNumber] == 0) {
            currentBitmap
        } else if (filterArrayList[currentImageNumber] == 2) {
            ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.GRAY)
        } else if (filterArrayList[currentImageNumber] == 2)
            ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.HDR)
        else {
            ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.SOFT_GLOW)
        }

        if (filterArrayList[currentImageNumber] != 0) {
            prevBitmap?.recycle()
            prevBitmap = null
        }
    }

}