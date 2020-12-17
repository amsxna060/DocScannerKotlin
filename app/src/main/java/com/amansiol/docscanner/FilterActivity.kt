package com.amansiol.docscanner

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import net.alhazmy13.imagefilter.ImageFilter
import java.io.File

class FilterActivity : AppCompatActivity() {

    lateinit var imageNumberTextView: TextView
    lateinit var blackImageView: ImageView
    private lateinit var grayImageView: ImageView
    private lateinit var noneImageView: ImageView
    private lateinit var glowImageView: ImageView
    private lateinit var hdrImageView: ImageView
    private var noneBitmap: Bitmap? = null
    private var blackBitmap: Bitmap? = null
    private var glowBitmap: Bitmap? = null
    private var grayBitmap: Bitmap? = null
    private var hdrBitmap: Bitmap? = null
    private var currentBitmap: Bitmap? = null
    private lateinit var currentImageView: ImageView
    private lateinit var currentFile: File
    private var currentFilterNumber = 0
    private var currentImageNumber: Int = 0
    private lateinit var progressBar: ProgressBar
    private var filterArrayList: ArrayList<Int> = ArrayList(CreatingPdf.bitmapFileArray.size)

    /* currentFilterNumber values-
    * 0- None
    * 1- Black
    * 2- Gray
    * 3- HDR
    * 4- Glow
    * */

    override fun onBackPressed() {
        val intent: Intent = Intent(this, CropActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        imageNumberTextView = findViewById(R.id.image_number_to_filter)
        currentImageView = findViewById(R.id.image_to_filter)
        progressBar = findViewById(R.id.filter_image_load_progressbar)

        noneImageView = findViewById(R.id.noneImageView)
        blackImageView = findViewById(R.id.blackImageView)
        glowImageView = findViewById(R.id.glowImageView)
        hdrImageView = findViewById(R.id.hdrImageView)
        grayImageView = findViewById(R.id.grayImageView)

        setFilterArray()

        setCurrentFile()

        currentBitmap = BitmapFactory.decodeFile(currentFile.absolutePath)
        currentImageView.setImageBitmap(currentBitmap)
        setAppropriateFilter()
        noneBitmap = null
        glowBitmap = null
        blackBitmap = null
        glowBitmap = null
        hdrBitmap = null

        noneBitmap = decodeSampledBitmapFromFile(currentFile, 40, 40)
        getAllPreviews()
        setImageNumberText()
    }

    fun goToCreatingPdf(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val v = LayoutInflater.from(this).inflate(R.layout.save_all_dialog,null,false)
        val saveBtn: Button = v.findViewById(R.id.sure_save)
        val dontSave: Button = v.findViewById(R.id.dont_save)

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        saveBtn.setOnClickListener {
            val task: SaveAllFileTask = SaveAllFileTask(this, filterArrayList)
            task.execute()
        }

        dontSave.setOnClickListener {
            //nothing
            dialog.dismiss()
        }
    }

    fun goToPrevFile(view: View) {
        if (currentImageNumber > 0) {
            currentImageNumber--
            setCurrentFile()
            val task: FilterLoadOther = FilterLoadOther(currentFile, currentImageNumber, progressBar, filterArrayList)

            task.setCurrentBitmap(currentBitmap)
            task.setCurrentImageView(currentImageView)
            task.setSmallImageViews(noneImageView, blackImageView, grayImageView, hdrImageView, glowImageView)
            task.setSmallBitmaps(noneBitmap, blackBitmap, grayBitmap, hdrBitmap, glowBitmap)

            task.execute()
            setImageNumberText()
        }
    }

    fun goToNextFile(view: View) {
        if (currentImageNumber < CreatingPdf.bitmapFileArray.size - 1) {
            currentImageNumber++
            setCurrentFile()
            val task: FilterLoadOther = FilterLoadOther(currentFile, currentImageNumber, progressBar, filterArrayList)

            task.setCurrentBitmap(currentBitmap)
            task.setCurrentImageView(currentImageView)
            task.setSmallImageViews(noneImageView, blackImageView, grayImageView, hdrImageView, glowImageView)
            task.setSmallBitmaps(noneBitmap, blackBitmap, grayBitmap, hdrBitmap, glowBitmap)

            task.execute()
            setImageNumberText()
        }
    }

    private fun setAppropriateFilter() {
        currentBitmap = if (filterArrayList[currentImageNumber] == 1) {
            ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.BLOCK)
        } else if (filterArrayList[currentImageNumber] == 2) {
            ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.GRAY)
        } else if(filterArrayList[currentImageNumber] == 2)
            ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.HDR)
        else {
            ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.SOFT_GLOW)
        }
    }

    private fun setImageNumberText() {
        val imageNumberText = "${currentImageNumber + 1}/${CreatingPdf.bitmapFileArray.size}"
        imageNumberTextView.text = imageNumberText
    }

    fun applyNoneFilter(view: View) {
        currentBitmap = BitmapFactory.decodeFile(CreatingPdf.bitmapFileArray[currentImageNumber].absolutePath)
        currentImageView.setImageBitmap(currentBitmap)
        filterArrayList[currentImageNumber] = 0
        currentFilterNumber = 0
    }

    fun applyBlack(view: View) {
        currentBitmap = BitmapFactory.decodeFile(CreatingPdf.bitmapFileArray[currentImageNumber].absolutePath)
        var prevBitmap = currentBitmap
        currentBitmap = ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.BLOCK)
        prevBitmap?.recycle()
        prevBitmap = null
        currentImageView.setImageBitmap(currentBitmap)
        filterArrayList[currentImageNumber] = 1
        currentFilterNumber = 1
    }

    fun applyGray(view: View) {
        currentBitmap = BitmapFactory.decodeFile(CreatingPdf.bitmapFileArray[currentImageNumber].absolutePath)
        var prevBitmap: Bitmap? = currentBitmap
        currentBitmap = ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.GRAY)
        prevBitmap?.recycle()
        prevBitmap = null
        currentImageView.setImageBitmap(currentBitmap)
        filterArrayList[currentImageNumber] = 2
        currentFilterNumber = 2
    }

    fun applyHDR(view: View) {
        currentBitmap = BitmapFactory.decodeFile(CreatingPdf.bitmapFileArray[currentImageNumber].absolutePath)
        var prevBitmap: Bitmap? = currentBitmap
        currentBitmap = ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.HDR)
        prevBitmap?.recycle()
        prevBitmap = null
        currentImageView.setImageBitmap(currentBitmap)
        filterArrayList[currentImageNumber] = 3
        currentFilterNumber = 3
    }

    fun applyGlow(view: View) {
        currentBitmap = BitmapFactory.decodeFile(CreatingPdf.bitmapFileArray[currentImageNumber].absolutePath)
        var prevBitmap: Bitmap? = currentBitmap
        currentBitmap = ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.SOFT_GLOW)
        prevBitmap?.recycle()
        prevBitmap =  null
        currentImageView.setImageBitmap(currentBitmap)
        filterArrayList[currentImageNumber] = 4
        currentFilterNumber = 4
    }

    private fun getAllPreviews() {
        getBlackPreview()
        getGlowPreview()
        getGrayPreview()
        getNonePreview()
        getHDRPreview()
    }

    private fun getNonePreview() {
        noneBitmap = decodeSampledBitmapFromFile(currentFile,40,40)
        noneImageView.setImageBitmap(noneBitmap)
    }

    private fun getGlowPreview() {
        glowBitmap = ImageFilter.applyFilter(noneBitmap, ImageFilter.Filter.SOFT_GLOW)
        glowImageView.setImageBitmap(glowBitmap)
    }

    private fun getHDRPreview() {
        hdrBitmap = ImageFilter.applyFilter(noneBitmap, ImageFilter.Filter.HDR)
        hdrImageView.setImageBitmap(hdrBitmap)
    }

    private fun getGrayPreview() {
        grayBitmap = ImageFilter.applyFilter(noneBitmap, ImageFilter.Filter.GRAY)
        grayImageView.setImageBitmap(grayBitmap)
    }

    private fun getBlackPreview() {
        blackBitmap = ImageFilter.applyFilter(noneBitmap, ImageFilter.Filter.BLOCK)
        blackImageView.setImageBitmap(blackBitmap)
    }

    private fun setCurrentFile() {
        currentFile = CreatingPdf.bitmapFileArray[currentImageNumber]
    }

    private fun setFilterArray() {
        for (i in 0 until CreatingPdf.bitmapFileArray.size) {
             filterArrayList.add(0)
        }
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

            BitmapFactory.decodeFile(file.absolutePath,this)
        }
    }
}