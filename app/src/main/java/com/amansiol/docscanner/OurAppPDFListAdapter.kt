package com.amansiol.docscanner

import android.app.Activity
import android.app.AlertDialog
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

            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            val v = LayoutInflater.from(context).inflate(R.layout.choose_action, null, false)

            val renameTextView: TextView = v.findViewById(R.id.rename_action)
            val editTextView: TextView = v.findViewById(R.id.edit_action)

            builder.setView(v)

            val dialog: AlertDialog = builder.create()
            dialog.show()

            renameTextView.setOnClickListener {
                val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
                val view = LayoutInflater.from(context).inflate(R.layout.rename, null, false)
                alertDialogBuilder.setView(view)

                val saveBtn: Button = view.findViewById(R.id.rename_my_file)
                val cancelBtn: Button = view.findViewById(R.id.rename_canceling)
                val editText: EditText = view.findViewById(R.id.rename_file_name_for_pdf)
                val d: AlertDialog = alertDialogBuilder.create()
                d.show()

                saveBtn.setOnClickListener {
                    if (editText.text.toString().isEmpty()) {
                        Toast.makeText(context, "Enter a name", Toast.LENGTH_SHORT).show()
                    } else {
                        val newName: String = editText.text.toString()
                        renameFile(newName, file)
                        notifyDataSetChanged()
                        d.dismiss()
                    }
                }

                cancelBtn.setOnClickListener {
                    d.dismiss()
                }

                dialog.dismiss()
            }

            editTextView.setOnClickListener {
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

                dialog.dismiss()
            }
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

    private fun renameFile(newName: String, file: File) {
        val directory: File = File(
            file.parentFile.absolutePath
        )
        val from = File(directory, file.name)
        val to = File(directory, newName.trim().toString() + ".pdf")
        from.renameTo(to)
    }

    private fun sharePDF(file: File) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        val pdfUri: Uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file);
        sharingIntent.type = "application/pdf"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
        context.startActivity(Intent.createChooser(sharingIntent, "Share PDF using"))
    }

    private fun deleteFile(file: File) {

        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        val v = LayoutInflater.from(context).inflate(R.layout.delete_ask, null, false)
        alertDialogBuilder.setView(v)

        val deleteBtn: Button = v.findViewById(R.id.sure_delete)
        val cancelBtn: Button = v.findViewById(R.id.dont_delete)

        val dialog: AlertDialog = alertDialogBuilder.create()
        dialog.show()

        deleteBtn.setOnClickListener {
            file.delete()
            pdfArray.remove(file)
            MainActivity.pdfListAdapter.notifyDataSetChanged()
            Toast.makeText(context,"${file.name} deleted",Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
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