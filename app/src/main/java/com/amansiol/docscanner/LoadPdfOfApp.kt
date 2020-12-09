package com.amansiol.docscanner

import android.content.Context
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.DiffUtil
import java.io.File
import java.lang.Exception

class LoadPDFs(var context: Context, var progressBar: ProgressBar, var directory: File, var fileArray: ArrayList<File>) : AsyncTask<Void, Void, Void>() {

    override fun onPreExecute() {
        super.onPreExecute()
        progressBar.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            fillPdfArray(directory)
            return null
        } catch (e: Exception) {
            Log.e("Err","Loading pdfs failed in LoadPDF class")
        }
        return null
    }

    private fun fillPdfArray(dir: File) {
        for(file in dir.listFiles()) {
            if (file.isDirectory) {
                fillPdfArray(file)
            } else {
                if (file.name.endsWith(".pdf")) {
                    fileArray.add(file)
                }
            }
        }
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        progressBar.visibility = View.INVISIBLE
        MainActivity.pdfListAdapter.notifyDataSetChanged()
    }
}