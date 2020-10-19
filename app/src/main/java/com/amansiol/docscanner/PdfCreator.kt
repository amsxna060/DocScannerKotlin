package com.amansiol.docscanner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

//The below class converts the BitmapArray to the Pdf Document
class PdfCreator(var context: Context, var bitmapArray: ArrayList<Bitmap>, var filename: String) {

    fun createPDF() {
        val document: PdfDocument = PdfDocument()
        for(i in 0 until bitmapArray.size) {
            val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(592,842,i + 1).create()
            val page: PdfDocument.Page = document.startPage(pageInfo)

            var currentBitmap = bitmapArray[i]

            currentBitmap = getResizedBitmap(currentBitmap)

            //taking margins from left and right according to bitmap's height and width for adjusting the bitmap to the centre of the page irrespective of its size
            val left: Float = (592 - currentBitmap.width.toFloat()) / 2
            val top: Float = (842 - currentBitmap.height.toFloat()) / 2

            page.canvas.drawBitmap(currentBitmap,left,top,null)
            document.finishPage(page)
        }

        writeToFileSystem(document)
    }

    private fun getResizedBitmap(bitmap: Bitmap) : Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val resizedBitmap: Bitmap
        if (width < 592 && height < 842) {
            resizedBitmap =  bitmap
        } else if (width > 592 && height > 842) {
            val scaleWidth = 592 / width.toFloat()
            val scaleHeight = 842 / height.toFloat()
            val matrix = Matrix()
            matrix.postScale(scaleWidth, scaleHeight)
            resizedBitmap =  Bitmap.createBitmap(bitmap,0,0, width, height, matrix,false)
        } else if(width > 592) {
            val scaleWidth = 592 / width.toFloat()
            val matrix = Matrix()
            matrix.postScale(scaleWidth, scaleWidth)
            resizedBitmap = Bitmap.createBitmap(bitmap,0,0, width, height, matrix,false)
        } else if(height > 842) {
            //scale height
            val scaleHeight = 842 / height.toFloat()
            val matrix = Matrix()
            matrix.postScale(scaleHeight, scaleHeight)
            resizedBitmap = Bitmap.createBitmap(bitmap,0,0, width, height, matrix,false)
        } else {
            //For some unknown situation
            resizedBitmap = bitmap
        }
        return resizedBitmap
    }

    private fun writeToFileSystem(document: PdfDocument) {
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            try {
                val path =
                    Environment.getExternalStorageDirectory().absolutePath + "/${Constants.APP_FOLDER_NAME}"
                val dir = File(path)
                if (!dir.exists()) {
                    dir.mkdir()
                }
                val file: File = File(dir, "$filename.pdf")
                document.writeTo(FileOutputStream(file))
                document.close()
                Toast.makeText(context,"File saved",Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Something went wrong while saving PDF",Toast.LENGTH_SHORT).show()
                Log.e("Exception!!!!- ", e.toString())
            }
        } else {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),Constants.RC_WRITE_EXTERNAL_STORAGE)
        }
    }
}