package com.amansiol.docscanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ViewAllPdf : AppCompatActivity() {

    companion object {
        lateinit var progressBar: ProgressBar
        val pdfArray: ArrayList<File> = ArrayList()

        lateinit var pdfListAdapter: PDFListAdapter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.RC_READ_EXTERNAL_STORAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val task: PDFListTask = PDFListTask(this)
            task.execute()
        } else if(requestCode == Constants.RC_WRITE_EXTERNAL_STORAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Permission granted to write storage try your action again",Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_pdf)
        progressBar = findViewById(R.id.progressbar_pdf_list)

        pdfArray.clear()

        //Check whether we have the permission to read the external storage so we can run the pdfListTask in background as async and get the PDFs of file
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val task: PDFListTask = PDFListTask(this)
            task.execute()
        } else {
            //We don't have the permission to read the files in device hence we ask for it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.RC_READ_EXTERNAL_STORAGE)
        }

        val devicePdfRecycler: RecyclerView = findViewById(R.id.device_pdf_recycler)
        pdfListAdapter = PDFListAdapter(this@ViewAllPdf)
        devicePdfRecycler.adapter = pdfListAdapter
    }
}