package com.amansiol.docscanner

import android.content.Context
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File
import java.lang.Exception

class ConversionListTask(var context: Context,val type : String) : AsyncTask<Void,Void,String>(){
    override fun doInBackground(vararg params: Void?): String? {
        try {
            fillPdfArray(Environment.getExternalStorageDirectory())
        } catch (e: Exception) {
            Log.e("Err in fillPDFArray()",e.toString())
            return null
        }
        return "success"
    }
    private fun fillPdfArray(dir : File){
        for(file in dir.listFiles()) {
            if (file.isDirectory) {
                if (file.absolutePath.contentEquals("${Environment.getExternalStorageDirectory()}/${Constants.APP_FOLDER_NAME}")) {
                    //We are not interested in fetching the files of our created PDFs
                }
                else
                    fillPdfArray(file)
            } else {
                if (file.name.endsWith(type)) {
                    DeviceFile.FileArray.add(file)
                }
            }
        }
    }

    override fun onPostExecute(result : String?) {
        super.onPostExecute(result)
        if (result == null) {
            Toast.makeText(context,"Something went wrong!", Toast.LENGTH_SHORT).show()
        }
        DeviceFile.conversionListAdapter.notifyDataSetChanged()
    }
}