package com.amansiol.docscanner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/*
* A class to convert the PDF file to Images and store it to the external storage
* The class takes three parameters in its constructor
*       1. Context of App
*       2. The particular file which has to be converted
*       3. ArrayList of page numbers which are needed to be converted into
*          the Images eg.
*          val selectedPageNumbers = ArrayList<Int>()
*          selectedPageNumbers.add(0)  //The first page of PDF file is selected to be converted to image file
*          selectedPagesNumbers.add(1) //The second page is selected to be converted to image file
*
*
*  A typical usage of the class is shown below-
*  var pdfToImageConverter: PdfToImageConverter = PdfToImageConverter(this@YourActivity, myPdfFile, selectedPageNumbers)
*  pdfToImageConverter.convertSelectedPagesToImages()
*
* */

class PdfToImageConverter(
    var context: Context,
    var pdfFile: File,
    var listOfPageNumbers: ArrayList<Int>
) {

    //when converting PDF to images, we may need the file array of converted images for merging and editing purposes
    var returnedFileArray: ArrayList<File> = ArrayList()

    fun convertSelectedPagesToImages(): ArrayList<File> {
        val renderer = PdfRenderer(
            ParcelFileDescriptor.open(
                pdfFile,
                ParcelFileDescriptor.MODE_READ_ONLY
            )
        )

        for (i in listOfPageNumbers) {

            val page = renderer.openPage(i)

            val width: Int = context.resources.displayMetrics.densityDpi / 72 * page.width
            val height: Int = context.resources.displayMetrics.densityDpi / 72 * page.height

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            storeImage(bitmap, i)

            page.close()
        }

        return returnedFileArray
    }

    private fun storeImage(bitmap: Bitmap, bitmapNumber: Int) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val pictureFile: File = getOutputFile(bitmapNumber)
            try {
                val fos = FileOutputStream(pictureFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                fos.close()

                returnedFileArray.add(pictureFile)
            } catch (e: FileNotFoundException) {
                Log.d("error", "File not found: " + e.message)
                Toast.makeText(
                    context,
                    "Something went wrong while storing the page in memory",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: IOException) {
                Log.d("error", "Error accessing file: " + e.message)
                Toast.makeText(
                    context,
                    "Something went wrong while storing the page in memory",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e("error", e.toString())
                Toast.makeText(
                    context,
                    "Something went wrong while storing the page in memory",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constants.RC_WRITE_EXTERNAL_STORAGE
            )
        }
    }

    private fun getOutputFile(num: Int): File {

        val mediaStorageDir: File = File(Environment.getExternalStorageDirectory().absolutePath + "/${pdfFile.name}")

        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir()
        }

        val timeStamp: String = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
        val mediaFile: File
        val mImageName = "${timeStamp}_${num}.jpg"
        mediaFile = File(mediaStorageDir.path + File.separator + mImageName)
        return mediaFile
    }

    private fun isSDCardPresent(): Boolean {
        val isSDPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        val isSDSupportedDevice = Environment.isExternalStorageRemovable()

        return isSDSupportedDevice && isSDPresent
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap): String? {
        val cw = ContextWrapper(context.applicationContext)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "profile.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }
}