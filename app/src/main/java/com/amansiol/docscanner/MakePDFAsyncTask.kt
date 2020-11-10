package com.amansiol.docscanner

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import java.io.File

class MakePDFAsyncTask(var context: Context, var fileArray: ArrayList<File>, var fileName: String) : AsyncTask<Void, Void, Void>() {
    lateinit var progressDialog: ProgressDialog
    override fun onPreExecute() {
        super.onPreExecute()
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Saving, Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }

    override fun doInBackground(vararg params: Void?): Void? {
        if (fileName.endsWith(".pdf",true))
            fileName = fileName.substring(0,fileName.length - 4)

        val pdfCreator: PdfCreator = PdfCreator(context, fileArray,fileName)
        pdfCreator.createPDF()
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        progressDialog.show()
        Toast.makeText(context, "File saved!",Toast.LENGTH_SHORT).show()
        CreatingPdf.bitmapArray.clear()
        CreatingPdf.bitmapFileArray.clear()
        val currentActivity = context as Activity
        currentActivity.finish()
    }
}