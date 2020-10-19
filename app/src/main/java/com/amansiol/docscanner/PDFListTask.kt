package com.amansiol.docscanner

import android.content.Context
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import java.io.File
import java.lang.Exception

class PDFListTask(var context: Context) : AsyncTask<Void,Void,String>() {
    override fun doInBackground(vararg parentFolder: Void?): String? {
        try {
            fillPdfArray(Environment.getExternalStorageDirectory())
        } catch (e: Exception) {
            Log.e("Err in fillPDFArray()",e.toString())
            return null
        }
        return "success"
    }

    private fun fillPdfArray(dir: File) {
        for(file in dir.listFiles()) {
            if (file.isDirectory) {
                if (file.absolutePath.contentEquals("${Environment.getExternalStorageDirectory()}/${Constants.APP_FOLDER_NAME}")) {
                    //We are not interested in fetching the files of our created PDFs
                }
                else
                    fillPdfArray(file)
            } else {
                if (file.name.endsWith(".pdf")) {
                    ViewAllPdf.pdfArray.add(file)
                }
            }
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        if (result == null) {
            Toast.makeText(context,"Something went wrong!",Toast.LENGTH_SHORT).show()
        }
        ViewAllPdf.progressBar.visibility = View.GONE
        ViewAllPdf.pdfListAdapter.notifyDataSetChanged()
    }
}