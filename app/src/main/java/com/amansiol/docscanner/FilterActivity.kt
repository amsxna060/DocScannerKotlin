package com.amansiol.docscanner

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import net.alhazmy13.imagefilter.ImageFilter
import java.io.File

class FilterActivity : AppCompatActivity() {

    private lateinit var imageNumberTextView: TextView
    private lateinit var currentBitmap: Bitmap
    private lateinit var currentFile: File
    private lateinit var currentImageView: ImageView
    private var currentFilterNumber = 0
    var currentImageNumber: Int = 0

    lateinit var blackBitmap: Bitmap
    lateinit var glowBitmap: Bitmap
    lateinit var grayBitmap: Bitmap
    lateinit var hdrBitmap: Bitmap

    lateinit var blackImageView: ImageView
    lateinit var grayImageView: ImageView
    lateinit var noneImageView: ImageView
    lateinit var glowImageView: ImageView
    lateinit var hdrImageView: ImageView

    var filterArrayList: ArrayList<Int> = ArrayList(CreatingPdf.bitmapArray.size)

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

        noneImageView = findViewById(R.id.noneImageView)
        blackImageView = findViewById(R.id.blackImageView)
        glowImageView = findViewById(R.id.glowImageView)
        hdrImageView = findViewById(R.id.hdrImageView)
        grayImageView = findViewById(R.id.grayImageView)

        setFilterArray()

        setCurrentFile()
        currentBitmap = BitmapFactory.decodeFile(currentFile.absolutePath)
        currentImageView.setImageBitmap(currentBitmap)
        getAllPreviews()
        setImageNumberText()
    }

    fun goToCreatingPdf(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        builder.setTitle("Save all work?")
            .setMessage("Are you sure you want to proceed?")
            .setPositiveButton("Yes"){_, _ ->
                val task: SaveAllFileTask = SaveAllFileTask(this, filterArrayList)
                task.execute()
            }
            .setNegativeButton("No", null)

        val dialog = builder.create()
        dialog.show()
    }

    fun goToPrevFile(view: View) {
        if (currentImageNumber > 0) {
            currentImageNumber--
            setCurrentFile()
            currentBitmap = BitmapFactory.decodeFile(currentFile.absolutePath)
            setAppropriateFilter()
            currentImageView.setImageBitmap(currentBitmap)
            getAllPreviews()
            setImageNumberText()
        }
    }

    fun goToNextFile(view: View) {
        if (currentImageNumber < CreatingPdf.bitmapArray.size - 1) {
            currentImageNumber++
            setCurrentFile()
            currentBitmap = BitmapFactory.decodeFile(currentFile.absolutePath)
            setAppropriateFilter()
            currentImageView.setImageBitmap(currentBitmap)
            getAllPreviews()
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
        val imageNumberText = "${currentImageNumber + 1}/${CreatingPdf.bitmapArray.size}"
        imageNumberTextView.text = imageNumberText
    }

//    private fun saveCurrentBitmapToFile() {
//        val task: SaveBitmapAsync = SaveBitmapAsync(CropActivity.currentBitmap, currentFile)
//        task.execute()
//    }

    fun applyNoneFilter(view: View) {
        currentBitmap = BitmapFactory.decodeFile(CreatingPdf.bitmapFileArray[currentImageNumber].absolutePath)
//        currentBitmap = BitmapFactory.decodeFile(currentFile.absolutePath)
        currentImageView.setImageBitmap(currentBitmap)
        currentImageView.setImageBitmap(currentBitmap)
        filterArrayList[currentImageNumber] = 0
        currentFilterNumber = 0
    }

    fun applyBlack(view: View) {
        currentBitmap = BitmapFactory.decodeFile(CreatingPdf.bitmapFileArray[currentImageNumber].absolutePath)
//        currentBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().absolutePath + "/${Constants.APP_FOLDER_NAME}/temp_$currentImageNumber.png")
        currentBitmap = ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.BLOCK)
        currentImageView.setImageBitmap(currentBitmap)
        filterArrayList[currentImageNumber] = 1
        currentFilterNumber = 1
    }

    fun applyGray(view: View) {
        currentBitmap = BitmapFactory.decodeFile(CreatingPdf.bitmapFileArray[currentImageNumber].absolutePath)
//        currentBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().absolutePath + "/${Constants.APP_FOLDER_NAME}/temp_$currentImageNumber.png")
        currentBitmap = ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.GRAY)
        currentImageView.setImageBitmap(currentBitmap)
        filterArrayList[currentImageNumber] = 2
        currentFilterNumber = 2
    }

    fun applyHDR(view: View) {
        currentBitmap = BitmapFactory.decodeFile(CreatingPdf.bitmapFileArray[currentImageNumber].absolutePath)
        currentBitmap = ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.HDR)
        currentImageView.setImageBitmap(currentBitmap)
        filterArrayList[currentImageNumber] = 3
        currentFilterNumber = 3
    }

    fun applyGlow(view: View) {
        currentBitmap = BitmapFactory.decodeFile(CreatingPdf.bitmapFileArray[currentImageNumber].absolutePath)
//        currentBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().absolutePath + "/${Constants.APP_FOLDER_NAME}/temp_$currentImageNumber.png")
        currentBitmap = ImageFilter.applyFilter(currentBitmap, ImageFilter.Filter.SOFT_GLOW)
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
//        val file: File = File(Environment.getExternalStorageDirectory().absolutePath + "/${Constants.APP_FOLDER_NAME}/temp_$currentImageNumber.png")
//        noneBitmap = decodeSampledBitmapFromFile(file,40,40)
        noneImageView.setImageBitmap(CreatingPdf.bitmapArray[currentImageNumber])
    }

    private fun getGlowPreview() {
//        val file: File = File(Environment.getExternalStorageDirectory().absolutePath + "/${Constants.APP_FOLDER_NAME}/temp_$currentImageNumber.png")
//        glowBitmap = decodeSampledBitmapFromFile(file,40,40)
        glowBitmap = ImageFilter.applyFilter(CreatingPdf.bitmapArray[currentImageNumber], ImageFilter.Filter.SOFT_GLOW)
        glowImageView.setImageBitmap(glowBitmap)
    }

    private fun getHDRPreview() {
//        val file: File = File(Environment.getExternalStorageDirectory().absolutePath + "/${Constants.APP_FOLDER_NAME}/temp_$currentImageNumber.png")
//        hdrBitmap = decodeSampledBitmapFromFile(file,40,40)
        hdrBitmap = ImageFilter.applyFilter(CreatingPdf.bitmapArray[currentImageNumber], ImageFilter.Filter.HDR)
        hdrImageView.setImageBitmap(hdrBitmap)
    }

    private fun getGrayPreview() {
      //  val file: File = File(Environment.getExternalStorageDirectory().absolutePath + "/${Constants.APP_FOLDER_NAME}/temp_$currentImageNumber.png")
      //  grayBitmap = decodeSampledBitmapFromFile(file,40,40)

        grayBitmap = ImageFilter.applyFilter(CreatingPdf.bitmapArray[currentImageNumber], ImageFilter.Filter.GRAY)
        grayImageView.setImageBitmap(grayBitmap)
    }

    private fun getBlackPreview() {
//        val file: File = File(Environment.getExternalStorageDirectory().absolutePath + "/${Constants.APP_FOLDER_NAME}/temp_$currentImageNumber.png")
//        blackBitmap = decodeSampledBitmapFromFile(file,40,40)
        blackBitmap = ImageFilter.applyFilter(CreatingPdf.bitmapArray[currentImageNumber], ImageFilter.Filter.BLOCK)
        blackImageView.setImageBitmap(blackBitmap)
    }

    private fun setCurrentFile() {
        currentFile = CreatingPdf.bitmapFileArray[currentImageNumber]
    }

    private fun setFilterArray() {
        for (i in 0 until CreatingPdf.bitmapArray.size) {
             filterArrayList.add(0)
        }
    }
}