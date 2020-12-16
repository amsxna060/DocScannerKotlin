package com.amansiol.docscanner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class OurAppPDFListAdapter(var context: Context, var pdfArray: ArrayList<File>) :  RecyclerView.Adapter<OurAppPDFListAdapter.OurPDFViewHolder>(), Filterable{
    var pdfArrayFull: ArrayList<File> = ArrayList()
    init {
        pdfArrayFull.addAll(pdfArray)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OurPDFViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.recent_pdf_row, parent,false)
        return OurPDFViewHolder(view)
    }

    override fun onBindViewHolder(holder: OurPDFViewHolder, position: Int) {
        val file: File = pdfArray[position]
        holder.fileNameTextView.text = file.name

        //Get the last modified date of the PDF file (creation date cannot be fetched)
        val date: Date = Date(file.lastModified())
        holder.fileCreationDate.text = date.toString()

        //Allow sharing of PDF when shareImageView is pressed
        holder.shareImageView.setOnClickListener(View.OnClickListener {
            sharePDF(file)
        })

        //delete PDF
        holder.deleteImageView.setOnClickListener(View.OnClickListener {
            PDFListAdapter.fileToBeDeleted = file
            //Check whether we have the permission to delete the files on device by checking write external storage permission
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                deleteFile(file)
            } else {
                //Ask for permission
                ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),Constants.RC_WRITE_EXTERNAL_STORAGE)
            }
        })

        //To edit PDF
        holder.editImageView.setOnClickListener(View.OnClickListener {

            PDFListAdapter.fileToBeDeleted = null
            val listOfPages: ArrayList<Int> = ArrayList()
            val renderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
            val noOfPages = renderer.pageCount

            for (i in 0 until noOfPages) {
                listOfPages.add(i)
            }
            val converter: PdfToImageConverter = PdfToImageConverter(context, file, listOfPages)
            CreatingPdf.bitmapFileArray = converter.convertSelectedPagesToImages()
            val intent: Intent = Intent(context, CropActivity::class.java)
            context.startActivity(intent)
        })

        //In general onClick listener on a listItem to show the PDF
        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(context,PdfViewActivity::class.java)
            intent.putExtra(Constants.INTENT_POSITION_KEY_NAME, position)
            intent.putExtra(Constants.IS_FROM_DEVICE,false)
            context.startActivity(intent)
            Animatoo.animateSplit(context)
        })
    }

    override fun getItemCount(): Int {
        return pdfArray.size
    }

    private fun sharePDF(file: File) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        val pdfUri: Uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file);
        sharingIntent.type = "application/pdf"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
        context.startActivity(Intent.createChooser(sharingIntent, "Share PDF using"))
    }

    private fun deleteFile(file: File) {
        file.delete()
        pdfArray.remove(file)
        MainActivity.pdfListAdapter.notifyDataSetChanged()
        Toast.makeText(context,"${file.name} deleted",Toast.LENGTH_SHORT).show()
    }

    class OurPDFViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileNameTextView: TextView = itemView.findViewById(R.id.file_name)
        val fileCreationDate: TextView = itemView.findViewById(R.id.file_date)
        val fileThumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        val deleteImageView: ImageView = itemView.findViewById(R.id.file_del)
        val shareImageView: ImageView = itemView.findViewById(R.id.file_share)
        val editImageView: ImageView = itemView.findViewById(R.id.file_edit_name)
    }

    override fun getFilter(): Filter {
        return filter
    }

    private val filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredFiles : ArrayList<File> = ArrayList()

            if (constraint == null || constraint.isEmpty()) {
                filteredFiles.addAll(pdfArrayFull)
            } else {
                for (file in pdfArrayFull) {
                    if (file.name.contains(constraint)) {
                        filteredFiles.add(file)
                    }
                }
            }

            val results : FilterResults = FilterResults()
            results.values = filteredFiles

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            pdfArray.clear()
            pdfArray.addAll(results?.values as ArrayList<File>)
            notifyDataSetChanged()
        }
    }

}