package com.amansiol.docscanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import java.io.File

class PdfViewActivity : AppCompatActivity() {

    private lateinit var pdfFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)

        val pdfView: PDFView = findViewById(R.id.barteksc_pdfView)

        val position: Int = intent.getIntExtra(Constants.INTENT_POSITION_KEY_NAME, -1)
        val pdfFileIsFromDevice = intent.getBooleanExtra(Constants.IS_FROM_DEVICE, false)

        pdfFile = if (pdfFileIsFromDevice)
            ViewAllPdf.pdfArray[position]
        else {
            MainActivity.appPDFArray[position]
        }

        if (position != -1) {
            pdfView.fromFile(pdfFile).spacing(4).load()
        }
    }

}